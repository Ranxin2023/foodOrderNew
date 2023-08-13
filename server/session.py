import secrets


class Session:
    def __init__(self, sid=None) -> None:
        if not sid:
            self.sid=secrets.token_hex(32)
        else:
            self.sid=sid
        # print("sid in session is:{}".format(self.sid))
