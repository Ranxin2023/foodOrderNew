import secrets

from sessionDatabase import SessionDatabase


class Session:
    def __init__(self, sid=None) -> None:
        localhost="127.0.0.1"
        self.db_username = "root"
        self.db_password = "R@ndyli94041424"
        self.db = SessionDatabase(localhost, self.db_username, self.db_password)
        if not sid or len(sid)==0:
            self.sid=secrets.token_hex(32)
        else:
            self.sid=sid

    def store_in_db(self, args):
        return self.db.store_in_db([self.sid]+args)
    
    def exit_from_db(self, args):
        return self.db.logout_from_db([self.sid]+args)
        
    
