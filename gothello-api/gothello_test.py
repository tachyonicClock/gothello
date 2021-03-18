import unittest
import asyncio
from gothello.client import *
from gothello.player import *
import time

class TestGothelloClient(unittest.TestCase):
    def setUp(self):
        self.client = GothelloClient("http://localhost:8080")

    def test_new_game(self):
        self.client.new_game("private")
        self.client.new_game("public")
        self.client.new_game("single_player")

        with self.assertRaises(GothelloException):
            self.client.new_game("unknown")

    def test_game_state(self):
        game = self.client.new_game("private")
        state = self.client.game_state(game["id"])

    def test_join_game(self):
        self.client.new_game("public")
        game = self.client.open_game()

class TestGameRunner():

    def handle_line(self, player):
        """
        Read in a 'line' or element from the json array. 
        This line describes a move or it provides the expected board.
        """
        line = self.data.pop(0)
        instruction_type = line["type"]

        if instruction_type == "state":
            # If the element contains the state we check that the board matches
            self.test.assertEqual(line["board"], player.board())

        if instruction_type == "playStone":
            x, y = line["x"], line["y"]
            return player.play_stone(x, y)

        if instruction_type == "pass":
            return player.pass_turn()

        self.test.assertNotEqual(instruction_type, "winner", "The winner instructions should be at the end")
        return self.handle_line(player)


    def __init__(self, file, player, test):
        self.player = player
        self.file = file
        self.test = test

        with open(file) as json_file:
            self.data = json.load(json_file)

        @self.player.my_turn
        def my_turn(player):
            return self.handle_line(player)

        @self.player.game_over
        def my_turn(player):
            line = self.data.pop(0)
            self.test.assertEqual("winner", line["type"])
            self.test.assertEqual(Stone[line["winner"]], player.get_winner())


def time_usage(func):
    async def wrapper(*args, **kwargs):
        beg_ts = time.time()
        retval = await func(*args, **kwargs)
        end_ts = time.time()
        diff = end_ts - beg_ts
        print("%s() elapsed time: %f %f Hz" % (func.__name__, diff, 1/diff))
        return retval
    return wrapper

class TestGothelloPlayer(unittest.IsolatedAsyncioTestCase):
    async def asyncSetUp(self):
        self.client = GothelloClient("http://localhost:8080")
        self.game = self.client.new_game()
        self.black = await WebSocketPlayer("ws://localhost:8080", self.game["id"]).connect()
        self.white = await WebSocketPlayer("ws://localhost:8080", self.game["id"]).connect()

    async def wait_for_game_end(self):
        await asyncio.gather(self.black.listen(), self.white.listen())

    @time_usage
    async def test_game(self):
        TestGameRunner("cases/black_00.json", self.black, self)
        TestGameRunner("cases/white_00.json", self.white, self)
        await self.wait_for_game_end()

    async def test_decorators(self):
        self.decorator_test_counter = 0

        @self.black.my_turn
        @self.white.my_turn
        def pass_turn(player):
            self.decorator_test_counter += 1
            self.assertEqual(player.get_turn(), player.my_stones())
            return player.pass_turn()

        @self.black.opponent_turn
        @self.white.opponent_turn
        def opponent(player):
            self.assertNotEqual(player.get_turn(), player.my_stones())
            self.decorator_test_counter += 1

        @self.black.game_over
        @self.white.game_over
        def game_over(player):
            self.decorator_test_counter += 1

        await self.wait_for_game_end()
        self.assertEqual(self.black.get_winner(), Stone.DRAW)
        self.assertEqual(self.decorator_test_counter, 8, "A callback wasn't called as expected")

    async def asyncTearDown(self):
        # await self.black.resign()
        await self.black.close()
        await self.white.close()


if __name__ == '__main__':
    unittest.main()