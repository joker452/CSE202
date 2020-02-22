CXX = g++
CXXFLAGS = -Wall

.PHONY: test clean

eggdrop: eggdrop.o
	$(CXX) $(CXXFLAGS) -o eggdrop eggdrop.o
eggdrop.o: eggdrop.cpp
	$(CXX) $(CXXFLAGS) -c eggdrop.cpp

clean:
	rm -f *.o eggdrop compare
