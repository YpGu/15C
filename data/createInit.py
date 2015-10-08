# init.py: create random initializations for parameters

import random

def genRan(seed, t, maxRan):
#	t = 0.5
#	maxRan = 2

	fout = open('./init/init_p_' + str(seed), 'w')
	for i in range(100000):
		newline = ''
		for k in range(20):
			r = (random.random() - t) * maxRan
			newline = newline + str(r) + '\t'
		newline = newline + '\n'
		fout.write(newline)
	fout.close()

	fout = open('./init/init_q_' + str(seed), 'w')
	for i in range(100000):
		newline = ''
		for k in range(20):
			r = (random.random() - t) * maxRan
			newline = newline + str(r) + '\t'
		newline = newline + '\n'
		fout.write(newline)
	fout.close()

	fout = open('./init/init_b_' + str(seed), 'w')
	for i in range(100000):
		newline = ''
		for k in range(20):
			r = (random.random() - t) * maxRan
			newline = newline + str(r) + '\t'
		newline = newline + '\n'
		fout.write(newline)
	fout.close()

if __name__ == '__main__':
#	for i in range(0,5):
#		genRan(i, 0, 1)
#	for i in range(5,10):
#		genRan(i, 0, 1)
	for i in range(0,10):
		genRan(i, 0.5, 2)

