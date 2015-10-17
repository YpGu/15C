def prob(p, q, b, x, y):
	score = 0
	try:
		xs = p[x]
		ys = q[y]
		for i in range(len(xs)):
			score += xs[i] * ys[i]
		score += b[y]
	except KeyError:
		pass

	return score

def readParam(p, q, b, rel):
	seed = 5

	fin = open('../../src/1016_auto/saved_param/p_' + str(seed))
	lines = fin.readlines()
	for line in lines:
		ls = line.split('\t')
		x = int(ls[0])
		vs = []
		for i in range(len(ls)-1):
			v = float(ls[i+1])
			vs.append(v)
		p[x] = vs
	fin.close()

	fin = open('../../src/1016_auto/saved_param/q_' + str(rel) + '_' + str(seed))
	lines = fin.readlines()
	for line in lines:
		ls = line.split('\t')
		x = int(ls[0])
		vs = []
		for i in range(len(ls)-1):
			v = float(ls[i+1])
			vs.append(v)
		q[x] = vs
	fin.close()

	fin = open('../../src/1016_auto/saved_param/b_' + str(seed))
	lines = fin.readlines()
	for line in lines:
		ls = line.split('\t')
		x = int(ls[0])
		vs = []
		for i in range(len(ls)-1):
			v = float(ls[i+1])
			vs.append(v)
		b[x] = vs
	fin.close()

def evaluate1(p, q, b, rel):
	fin = open('./candidate' + str(rel))
	lines = fin.readlines()
	candidate = [int(i) for i in lines]
	print candidate

	record = {}
	if rel == 1:
		fin = open('../3k_mention_bipartite/mention_list_3k.test')
		lines = fin.readlines()
		i = 0
		for line in lines:
			x = int(line.split('\t')[0])
			if x in candidate:
				y = int(line.split('\t')[1])
				sim = prob(p, q, b, x, y)
				record[i] = [sim, 1]
				i += 1
		fin.close()

		fin = open('../3k_mention_bipartite/n_mention_list_3k.test.1')
		lines = fin.readlines()
		for line in lines:
			x = int(line.split('\t')[0])
			if x in candidate:
				y = int(line.split('\t')[1])
				sim = prob(p, q, b, x, y)
				record[i] = [sim, -1]
				i += 1
		fin.close()
	elif rel == 2:
		fin = open('../3k_retweet_bipartite/retweet_list_3k.test')
		lines = fin.readlines()
		i = 0
		for line in lines:
			x = int(line.split('\t')[0])
			if x in candidate:
				y = int(line.split('\t')[1])
				sim = prob(p, q, b, x, y)
				record[i] = [sim, 1]
				i += 1
		fin.close()

		fin = open('../3k_retweet_bipartite/n_retweet_list_3k.test.1')
		lines = fin.readlines()
		for line in lines:
			x = int(line.split('\t')[0])
			if x in candidate:
				y = int(line.split('\t')[1])
				sim = prob(p, q, b, x, y)
				record[i] = [sim, -1]
				i += 1
		fin.close()

	return record

def roc(rec):
	numPos = len([i for i in rec if rec[i][1] == 1])
	numNeg = len([i for i in rec if rec[i][1] == -1])
	print 'numPos =', numPos, 'numNeg =', numNeg

	ss = sorted(rec.items(), key = lambda x: x[1][0], reverse = True)
	newX = 0; oldX = 0; newY = 0; oldY = 0
	auc1 = 0; auc2 = 0
	for s in ss:
		label = s[1][1]
		if label == 1:
			newY += 1/float(numPos)
		elif label == -1:
			newX += 1/float(numNeg)
		auc1 += (newX - oldX) * newY
		auc2 += (newX - oldX) * oldY
		oldX = newX; oldY = newY
	print 'newY = ' + str(newY) + ' newX = ' + str(newX)
	print 'auc between ' + str(auc1) + ' and ' + str(auc2)

if __name__ == '__main__':
	p = {}; q = {}; b = {}
	rel = 2
	readParam(p, q, b, rel);
	rec = evaluate1(p, q, b, rel)
	roc(rec)

