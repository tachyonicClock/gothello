import neat
import pickle
import gothello
import asyncio
from contendor import Contendor


async def main():
    net = pickle.load(open("genome-50", "rb"))
    contendor = Contendor(net)
    gg = gothello.GothelloGame(contendor.run_model, None)
    await gg.play_against_human()

if __name__ == '__main__':
    asyncio.run(main())
