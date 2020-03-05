CXX = g++
CXXFLAGS = -Wall -std=c++11

.PHONY: test clean

tile: tile.o
	$(CXX) $(CXXFLAGS) -o tile tile.o
tile.o: tile.cpp
	$(CXX) $(CXXFLAGS) -c tile.cpp

clean:
	rm -f *.o tile