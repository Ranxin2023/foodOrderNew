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
        sid = request.cookies.get('sid')
        resp=None
        request.session=Session(sid=sid)
        print("sid after generating is:{}".format(request.session.sid))
        if request.method == "POST":
            request_body = request.get_json()
            method=request_body["method"]
            data=request_body["args"]
            resp= self.handle_request(method, data, request.session.sid)
            expires = datetime.datetime.now() + datetime.timedelta(days=365)
            resp.set_cookie("sid", request.session.sid, expires=expires)
        else:
            resp= Response("Method Not Allowed", status=405)
        return resp
    
    def handle_request(self, method, data,sid):
        response_data=None
        if method=="init":
            if sid != None:
                response_data=json.dumps({"success": True})
                print("store successfully")
            else:
                response_data=json.dumps({"success":False})
        if method=="ordering":
            args=[data[0], data[1]]
            args.extend(self.calculate_cost(data[0], data[1]))
            reponse_from_db = self.db.send_order(args)
            response_data = json.dumps(
                {"total price": args[4], "commit": reponse_from_db[0]}
            )
            
            
        if method=="view order":
            args = [data[0]]
            response_from_db = self.db.get_order(args)
            response_data = json.dumps(
                {
                    "commit": response_from_db[0],
                    "response data": response_from_db[1],
                }
            )
            print("response data")
            print(response_from_db[1])
        return Response(
            response_data, content_type="application/json", status=200
        )
        

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
