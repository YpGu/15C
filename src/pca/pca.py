import numpy as np
import sys
from sklearn.decomposition import PCA

def getPCA(fileDir):
	fin = open(fileDir)
#	print 'Reading ' + fileDir + ' ...'
	lines = fin.readlines()
	idp = {}
	for line in lines:
		ls = line.split('\t')
		uid = int(ls[0])
		feature = []
		for i in range(5):
			feature.append(float(ls[i+1]))
		idp[uid] = feature
	fin.close()
#	print 'Reading done. len = ' + str(len(idp))

	X = np.array([idp[i] for i in idp])
	pca = PCA(n_components=1)
	pca.fit(X)
	fc = pca.transform(X)

	count = 0
	for i in idp:
		idp[i] = fc[count].tolist()[0]
		count += 1
	#	print idp[i]
	#	gu = raw_input()

	return idp

def getCand():
	fin = open('../../data/dict/merge_id_list')
	lines = fin.readlines()
	candidates = {}
	for line in lines:
		uid = int(line.split('\t')[1])
		party = line.split('\t')[3].split('\n')[0]
		candidates[uid] = party
	fin.close()

	return candidates

def calcAUC(idp):
	candidates = getCand()
#	print len(candidates)

	numR = len({k:v for (k,v) in idp.iteritems() if k in candidates and candidates[k] == "R"})
	numD = len({k:v for (k,v) in idp.iteritems() if k in candidates and candidates[k] == "D"})
#	print 'num of R = ' + str(numR) + ', num of D = ' + str(numD)

	idp_filtered = {k:v for (k,v) in idp.iteritems() if k in candidates}
#	print 'Length of people in the filtered list = ' + str(len(idp_filtered))
#	print len(idp_filtered)
	sortedS = sorted(idp_filtered.items(), key = lambda x: x[1], reverse = True)

	# treat D as positive label
	if True:
		newX = 0; oldX = 0; newY = 0; oldY = 0
		auc1 = 0; auc2 = 0
		for i in sortedS:
			uid = i[0]
			score = i[1]
			if candidates[uid] == "R":
				newX += 1.0/numR
			elif candidates[uid] == "D":
				newY += 1.0/numD
			auc1 += (newX - oldX) * newY
			auc2 += (newX - oldX) * oldY
			oldX = newX; oldY = newY
#		print '------------------'
#		print 'newX = ' + str(newX) + ', newY = ' + str(newY)
#		print 'auc = ' + str(auc1) + ', ' + str(auc2)
		aucA = (auc1+auc2)/2

	# treat R as positive label
	if True:
		newX = 0; oldX = 0; newY = 0; oldY = 0
		auc1 = 0; auc2 = 0
		for i in sortedS:
			uid = i[0]
			score = i[1]
			if candidates[uid] == "D":
				newX += 1.0/numD
			elif candidates[uid] == "R":
				newY += 1.0/numR
			auc1 += (newX - oldX) * newY
			auc2 += (newX - oldX) * oldY
			oldX = newX; oldY = newY
#		print 'newX = ' + str(newX) + ', newY = ' + str(newY)
#		print 'auc = ' + str(auc1) + ', ' + str(auc2)
#		print '------------------\n'
		aucB = (auc1+auc2)/2
	if aucA > 0.5:
		print aucA,
	else:
		print aucB,

if __name__ == "__main__":
	if len(sys.argv) != 2:
		print 'Try again. Usage: python pca.py <dir>'
		sys.exit()
#		print sys.argv[1]
#	idp = getPCA('../f3/saved_param/q_1_5')
	idp = getPCA(sys.argv[1])
	calcAUC(idp)


