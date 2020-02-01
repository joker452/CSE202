CXX = g++
CXXFLAGS = -Wall

.PHONY: test clean

evenpath: evenpath.o
	$(CXX) $(CXXFLAGS) -o evenpath evenpath.o
evenpath.o: evenpath.cpp
	$(CXX) $(CXXFLAGS) -c evenpath.cpp

test: evenpath compare

compare: compare.o
	$(CXX) $(CXXFLAGS) -o compare compare.o
compare.o: compare.cpp
	$(CXX) $(CXXFLAGS) -c compare.cpp
clean:
	rm -f *.o evenpath compare
