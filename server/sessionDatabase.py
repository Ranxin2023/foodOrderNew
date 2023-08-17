import pymysql


class SessionDatabase:
    def __init__(self, host=None, user=None, password=None):
        self.host = host or "localhost"
        self.user = user
        self.password = password

        self._conn = None
        self.cur_db_name = "foodorder"
        self.table_name = "session"
        self._cursor = None
        self.connect_db()
        self.use_ssl = False
        self.ssl = {}

    def connect_db(self):
        self._conn = pymysql.connect(
            host=self.host,
            user=self.user,
            password=self.password,
            db=self.cur_db_name,
        )

        self._cursor = self._conn.cursor()

    def store_in_db(self, args):
        sid=str(args[0])
        username=str(args[1])
        login_time=str(args[2])
        cmd = "INSERT INTO  {} \
            (sid, username, logintime, status) \
                VALUES(%s, %s, %s, %s)".format(
            self.table_name
        )
        try:
            self._cursor.execute(
                cmd,
                (
                    sid,
                    username,
                    login_time,
                    "login"
                ),
            )
            self._conn.commit()
            return True, None
        except pymysql.Error as e:
            print("Error sending order to the database:", e)
            self._conn.rollback()
            return False, str(e)

    def logout_from_db(self, args):
        sid=str(args[0])
        logout_time=str(args[1])
        cmd = "UPDATE {} SET logouttime = %s, status = %s WHERE sid = %s".format(self.table_name)
    
        try:
            self._cursor.execute(
                cmd,
                (
                    logout_time, 
                    "logout", 
                    sid,
                ),
            )
            self._conn.commit()
            return True, None
        except pymysql.Error as e:
            print("Error updating database:", e)
            self._conn.rollback()
            return False, str(e)
