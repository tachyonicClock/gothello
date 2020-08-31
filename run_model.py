import neat
import pickle
import gothello
import asyncio
from progress.bar import Bar
from contendor import Contendor
from os import walk
import itertools

'''
229-nameless-sun.pkl           12.50%
315-wispy-butterfly.pkl        16.67%
266-throbbing-frost.pkl        20.83%
damp-silence.pkl               25.00%
hidden-paper.pkl               25.00%
delicate-lake.pkl              25.00%
291-still-glitter.pkl          25.00%
233-misty-wind.pkl             29.17%
310-long-frog.pkl              29.17%
251-muddy-sun.pkl              29.17%
305-broken-sky.pkl             33.33%
damp-frost.pkl                 41.67%
286-dawn-resonance.pkl         45.83%
261-lively-resonance.pkl       54.17%
296-purple-glade.pkl           58.33%
246-black-resonance.pkl        62.50%
276-patient-smoke.pkl          66.67%
bold-sun.pkl                   70.83%
238-autumn-silence.pkl         70.83%
301-long-meadow.pkl            75.00%
256-little-wind.pkl            79.17%
281-blue-field.pkl             79.17%
241-winter-glade.pkl           83.33%
271-polished-rain.pkl          91.67%
'''



async def main(model_name):
    a = Contendor(pickle.load(open("networks/" + model_name, "rb")))
    gg = gothello.GothelloGame(a.run_model, None)

    print("Game Started Against '{}' http://localhost:3000/game/{}".format(model_name, gg.id))
    await gg.play_against_human()


async def dual_models(name_a, name_b):
    a = Contendor(pickle.load(open("networks/" + name_a, "rb")))
    b = Contendor(pickle.load(open("networks/" + name_b, "rb")))

    gg = gothello.GothelloGame(a.run_model, b.run_model)
    win = await gg.play_game()

    if win == "A":
        print(name_a, "is the winner over", name_b)
    elif win == "B":
        print(name_b, "is the winner over", name_a)
    elif win == "AB":
        print(name_a, name_b, "drew")
    return win

async def network_tournament():
    f = []
    scores = {}
    for (dirpath, dirnames, filenames) in walk("networks"):
        f.extend(filenames)
        break
    for name in f:
        scores[name] = 0.0
    num_contendors = len(f)
    matches = list(itertools.combinations(f, 2))
    num_matches = len(matches)

    print("Running", num_matches, "games")
    for a_model, b_model in matches:
        win = await dual_models(a_model, b_model)
        if win == "A":
            scores[a_model] += 1
        elif win == "B":
            scores[b_model] += 1
        elif win == "AB":
            scores[a_model] += 0.5
            scores[b_model] += 0.5
    
    scores = sorted(scores.items(), key=lambda x: x[1])
    for name, score in scores:
        print("{:30} {:2.2f}%".format(name, score/num_contendors * 100))


# asyncio.run(dual_models("315-wispy-butterfly.pkl", "274-bold-moon.pkl"))
asyncio.run(main("315-wispy-butterfly.pkl"))
# asyncio.run(network_tournament())


# if __name__ == '__main__':
#     asyncio.run(main())
