import unittest
import asyncio
from gothello_client import *


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


class TestGothelloPlayer(unittest.IsolatedAsyncioTestCase):
    async def asyncSetUp(self):
        self.client = GothelloClient("http://localhost:8080")
        self.game = self.client.new_game()
        self.black = await GothelloWSPlayer("ws://localhost:8080", self.game["id"]).connect()
        self.white = await GothelloWSPlayer("ws://localhost:8080", self.game["id"]).connect()

    async def run_recorded(self, player, file):
        """Run a JSON file of moves and check that the board state is correct"""
        with open(file) as json_file:
            data = json.load(json_file)
            for row in data:
                await player.wait_for_my_turn()

                row_type = row["type"]
                print(row_type)
                if row_type == "state":
                    self.assertEqual(row["board"], player.board())
                if row_type == "playStone":
                    x, y = row["x"], row["y"]
                    await player.play_stone(x, y)
                if row_type == "pass":
                    await player.pass_turn()
                if row_type == "winner":
                    self.assertEqual(Stone[row["winner"]], player.get_winner())


    async def test_play_stone(self):
        await self.black.wait_for_my_turn()
        await self.black.play_stone(7,0)
        self.assertEqual(self.black.get_square(7,0), self.black.my_stones())

    async def test_play_stone_two(self):
        a = self.run_recorded(self.black, "cases/black_00.json")
        b = self.run_recorded(self.white, "cases/white_00.json")
        await asyncio.gather(a, b)
        

    async def asyncTearDown(self):
        await self.black.resign()
        await self.black.close()
        await self.white.close()


if __name__ == '__main__':
    unittest.main()