class GothelloException(Exception):
    def __init__(self, msg):
        if msg["messageType"] != "status":
            super().__init__("Unexpected message type")
            return

        err = "{} {}".format(msg["variant"], msg["message"])
        super().__init__(err)

    @classmethod
    def check_msg_for_exception(cls, msg, expected_type):
        if msg["messageType"] != expected_type:
            raise GothelloException(msg)