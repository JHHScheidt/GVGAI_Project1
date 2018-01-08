import numpy as np
import pickle

#Input would be array, have to split 
#Input = Array of states(activations and actions), put for-loop around it. Also contains reward
# Let's rework 

class Qlearner(object):
    alpha = 0.1
    gamma = 0.9
    lambda_ = 0.9
    #dont know how to properly init this
    oldCoordinate=0
    #has to be initialized in a proper way
	discretiseConstant=1000
	dimCount=0
    
    qTable = np.empty([1])
	#np.random.rand(discretiseConstant, discretiseConstant, discretiseConstant)*1e-4 #maybe 1e-5
    
    def __init__(self):
        self.makeShape(dimensions ,self.discretiseconstant)
        print("...")
    
	def makeShape(self, nDimensions, discConstant)
		discretiseConstant=discConstant
		dimCount=nDimensions
		dim = []
		for i in range(nDimensions):
			dim.append(discretiseConstant)
		dimTuple= tuple(dim)
		self.qTable = np.ndarray(shape=(dimTuple), dtype=float, order='F'),np.zeros
	
    def updateQValue(self, coord, reward):
        #update Q-Value
        if self.oldCoordinate==0:
            self.oldCoordinate = coord
        else:
			#not the max value, just use the Qvalue for the next state
			self.qTable[self.oldCoordinate]+=self.alpha*(reward+self.gamma*self.qTable[coord]- self.qTable[self.oldCoordinate])
			self.oldCoordinate = coord	# Should we do e(s,a)?
        
    def qLearning(self, coord, reward):
        mappedCoord= self.mapCoordinate(coord)
        self.updateQValue(mappedCoord, reward)
        
    def mapCoordinate(self, coordinate):
        mappedCoordinate=np.math.floor(coordinate * self.discretiseConstant)
        return mappedCoordinate
    
    #def fitTable():
    
        
    def saveQ(self):
	
        f = open("QOutput.txt","w")
		output = rekursion(list,out)
		f.write(output)
		f.close
        #output += "{"
        #for x in range(self.discretiseConstant):
        #    output += "{"
        #    for y in range(self.discretiseConstant):
        #        output += "{"
        #        for z in range(self.discretiseConstant):
        #            output+=(str(self.qTable[x,y,z]))
        #            if z!=self.discretiseConstant-1:
        #                output += ","
        #        output += "}"
        #    output += "}"
        #output += "}"       
		#
        #f.write(output)
        #f.close()
        #TODO
        #print qtabable in .txt file
        
        #with open("QTable.file", "wb") as f:
        #pickle.dump(self.qTable, f, pickle.HIGHEST_PROTOCOL)
        
  
    def rekursion(self, list, output):
        if len(list)!=self.dimCount:
            for i in range(self.discretiseConstant):
                output+="["
                rekursion(list.append(i), output)
                output+="]"
        else:
            for i in range(self.discretiseConstant):
				output+="["
                output+=str(self.qTable[list])
				output+="]"
                
		if len(list)==0:
			return output
    
    
    
    
    
    
    
# returns the index for pos and speed to find the current state in the Qtable
#def getState(observation):
#    posIdx = np.math.floor(((observation[0] - minPos) / (maxPos - minPos)) * discretiseConst)
#    speedIdx = np.math.floor(((observation[1] - minSpeed) / (maxSpeed - minSpeed)) * discretiseConst)
#    return posIdx, speedIdx


