import sys

def input(pi):
	for i in range(10):
		fin = open('./0930r/log_retweet/' + str(i) + '/' + pi + '.log')
		line = fin.readlines()[0]
		print line
#		print len(line.split('\t'))

if __name__ == '__main__':
	if len(sys.argv) != 2:
		print 'Usage: python xlsxInput.py <pi>'
		sys.exit()
	pi = sys.argv[1]
	input(pi)
