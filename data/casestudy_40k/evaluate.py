import sys

def prob(p, q, b, x, y):
	score = 0
	try:
		xs = p[x]
		ys = q[y]
		for i in range(len(xs)):
			score += xs[i] * ys[i]
		score += b[y]
	except KeyError:
		print 'err'
		pass

	return score

def readParam(p, q, b, rel, seed, option):

        ########### p ##############
        filedir = ''
        if option == 0:
                filedir = '../../src/40k/saved_param/p_' + str(seed)
        elif option == 1:
                filedir = '../../src/40k_111/saved_param/p_' + str(seed)
        elif option == 2:
                filedir = '../../src/40k_010/saved_param/p_' + str(seed)
        elif option == 3:
                filedir = '../../src/40k_001/saved_param/p_' + str(seed)
        fin = open(filedir)

#	fin = open('../../src/40k/saved_param/p_' + str(seed))                  # auto (all) 
#	fin = open('../../src/40k_111/saved_param/p_' + str(seed))              # fixed (all)
#	fin = open('../../src/40k_010/saved_param/p_' + str(seed))              # single network (mention)
#	fin = open('../../src/40k_001/saved_param/p_' + str(seed))              # single network (retweet) 
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

        ########### q ##############
        if option == 0:
                filedir = '../../src/40k/saved_param/q_' + str(rel) + '_' + str(seed)         # auto (all)
        elif option == 1:
        	filedir = '../../src/40k_111/saved_param/q_' + str(rel) + '_' + str(seed)     # fixed (all)
        elif option == 2:
                filedir = '../../src/40k_010/saved_param/q_' + str(rel) + '_' + str(seed)     # single network (mention) 
        elif option == 3:
                filedir = '../../src/40k_001/saved_param/q_' + str(rel) + '_' + str(seed)     # single network (retweet) 
        fin = open(filedir)

#	fin = open('../../src/40k/saved_param/q_' + str(rel) + '_' + str(seed))         # auto (all)
#	fin = open('../../src/40k_111/saved_param/q_' + str(rel) + '_' + str(seed))     # fixed (all)
#	fin = open('../../src/40k_010/saved_param/q_' + str(rel) + '_' + str(seed))     # single network (mention) 
#	fin = open('../../src/40k_001/saved_param/q_' + str(rel) + '_' + str(seed))     # single network (retweet) 
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

	fin_d = open('../40k_all/all_dict_40k')
	allD = {}
	lines = fin_d.readlines()
	for line in lines:
		index, rawid = line.split('\t')
		index = int(index)
		rawid = int(rawid)
		allD[index] = rawid
	fin.close()

        if option == 0:
            filedir = '../../src/40k/saved_param/b_' + str(seed)              # all
        elif option == 1:
            filedir = '../../src/40k_111/saved_param/b_' + str(seed)
        elif option == 2:
            filedir = '../../src/40k_010/saved_param/b_' + str(seed)          # single (mention)
        elif option == 3:
            filedir = '../../src/40k_001/saved_param/b_' + str(seed)          # single (retweet) 
        fin = open(filedir)

#	fin = open('../../src/40k/saved_param/b_' + str(seed))              # all
#	fin = open('../../src/40k_111/saved_param/b_' + str(seed))          # all (fixed)
#	fin = open('../../src/40k_010/saved_param/b_' + str(seed))          # single (mention)
#	fin = open('../../src/40k_001/saved_param/b_' + str(seed))          # single (retweet) 
	lines = fin.readlines()
	for line in lines:
		ls = line.split('\t')
		for i in range(len(ls)-1):
			x = allD[i]
			v = float(ls[i+1])
			b[x] = v
	fin.close()

def evaluate1(p, q, b, rel):
	fin = open('./candidate' + str(rel))
	lines = fin.readlines()
	candidate = [int(i) for i in lines]
#	print candidate

	record = {}
	if rel == 1:
		fin = open('../40k_mention_bipartite/mention_list_40k.test')
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

		fin = open('../40k_mention_bipartite/n_mention_list_40k.test.3')
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
		fin = open('../40k_retweet_bipartite/retweet_list_40k.test')
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

		fin = open('../40k_retweet_bipartite/n_retweet_list_40k.test.3')
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
#	print 'numPos =', numPos, 'numNeg =', numNeg

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
#	print 'newY = ' + str(newY) + ' newX = ' + str(newX)
#	print 'auc between ' + str(auc1) + ' and ' + str(auc2)
	print str((auc1+auc2)*0.5)

if __name__ == '__main__':
	p = {}; q = {}; b = {}
	rel = int(sys.argv[1])          # 1 for mention and 2 for retweet 
	seed = int(sys.argv[2])         # different initializations 
        option = int(sys.argv[3])       # 0 for all; 1 for all (fixed); 2 for mention (single network); 3 for retweet (single network) 
	readParam(p, q, b, rel, seed, option);
	rec = evaluate1(p, q, b, rel)
	roc(rec)

