import neat
import pickle
import gothello
import asyncio
from progress.bar import Bar
from contendor import Contendor
from os import walk
import itertools

'''
233-misty-wind.pkl             15.38%
damp-silence.pkl               19.23%
229-nameless-sun.pkl           19.23%
delicate-lake.pkl              23.08%
266-throbbing-frost.pkl        23.08%
315-wispy-butterfly.pkl        23.08%
310-long-frog.pkl              26.92%
291-still-glitter.pkl          26.92%
251-muddy-sun.pkl              30.77%
305-broken-sky.pkl             38.46%
320-late-water.pkl             42.31%
274-bold-moon.pkl              42.31%
damp-frost.pkl                 42.31%
hidden-paper.pkl               46.15%
286-dawn-resonance.pkl         50.00%
261-lively-resonance.pkl       57.69%
bold-sun.pkl                   61.54%
296-purple-glade.pkl           61.54%
246-black-resonance.pkl        65.38%
238-autumn-silence.pkl         69.23%
276-patient-smoke.pkl          69.23%
301-long-meadow.pkl            73.08%
256-little-wind.pkl            76.92%
281-blue-field.pkl             76.92%
241-winter-glade.pkl           80.77%
271-polished-rain.pkl          88.46%
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
    for (dirpath, dirnames, filenames) in walk("best_networks"):
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


# asyncio.run(dual_models("320-late-water.pkl", "325-holy-glade.pkl"))
# asyncio.run(dual_models("320-late-water.pkl", "271-polished-rain.pkl"))
asyncio.run(dual_models("301-long-meadow.pkl", "357-wispy-sun.pkl"))


asyncio.run(main("357-wispy-sun.pkl"))
# asyncio.run(network_tournament())


# if __name__ == '__main__':
#     asyncio.run(main())
