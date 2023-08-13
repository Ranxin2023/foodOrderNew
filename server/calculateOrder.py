import datetime
import json
from typing import Any

from werkzeug.local import Local, LocalManager
from werkzeug.wrappers import Request, Response

from orderDatabase import OrderDatabase
from session import Session

local=Local()
Local_manager=LocalManager([local])
class App:
    def __init__(self) -> None:
        localhost="127.0.0.1"
        self.db_username = "root"
        self.db_password = "R@ndyli94041424"
        self.db = OrderDatabase(localhost, self.db_username, self.db_password)
        # self.sid=None

    def calculate_cost(self, french_fries_count, big_mac_count):
        french_fries_price = 1.95
        big_mac_price = 5.17
        french_fries_cost = french_fries_count * french_fries_price
        big_mac_cost = big_mac_count * big_mac_price
        total_price = french_fries_cost + big_mac_cost
        return [big_mac_cost, french_fries_cost, total_price]

    def init_request(self, request):
        local.request=request
        return self.handle_request(request)
    
    def handle_request(self, request):
        sid = request.cookies.get('sid')
        # print("cookies:{}".format(request.cookies))
        resp=None
        # print("session sid is:{}".format(local.request.session.sid))
        
        request.session=Session(sid=sid)
        print("sid after generating is:{}".format(request.session.sid))
        if request.method == "POST":
            # print("received")
            request_body = request.get_json()
            print("Received data from the client:")
            print(request_body)
            # handle each request
            if request_body["method"] == "ordering":
                # Process the data as needed (e.g., save to database, perform calculations)
                data = request_body["args"]
                args = [data[0], data[1]]
                # Send a response back to the client (e.g., confirming data received)
                args.extend(self.calculate_cost(data[0], data[1]))

                # print("total_price:", args[4])

                reponse_from_db = self.db.send_order(args)
                response_data = json.dumps(
                    {"total price": args[4], "commit": reponse_from_db[0]}
                )
                resp=Response(
                    response_data, content_type="application/json", status=200
                )
            if request_body["method"] == "view order":
                data = request_body["args"]
                args = [request_body["args"][0]]
                response_from_db = self.db.get_order(args)
                response_data = json.dumps(
                    {
                        "commit": response_from_db[0],
                        "response data": response_from_db[1],
                    }
                )
                print("response data")
                print(response_from_db[1])
                resp= Response(
                    response_data, content_type="application/json", status=200
                )
        else:
            resp= Response("Method Not Allowed", status=405)
        if resp:
            expires = datetime.datetime.now() + datetime.timedelta(days=365)
            resp.set_cookie("sid", request.session.sid, expires=expires)
            # print("response id:{}", resp.cookies.get('sid'))
        # print("response is:{}".format(resp))
        return resp

    def wsgi_app(self, environ, start_response):
        """WSGI application that processes requests and returns responses."""
        request = Request(environ)
        response = self.init_request(request)
        return response(environ, start_response)

    def __call__(self, environ, start_response):
        """The WSGI server calls this method as the WSGI application."""
        return self.wsgi_app(environ, start_response)


if __name__ == "__main__":
    from werkzeug.serving import run_simple

    run_simple("192.168.56.1", 80, App())
