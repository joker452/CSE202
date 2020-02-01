CXX = g++
CXXFLAGS = -Wall

.PHONY: test clean

checkmatrix: checkmatrix.o
	$(CXX) $(CXXFLAGS) -o checkmatrix checkmatrix.o
checkmatrix.o: checkmatrix.cpp
	$(CXX) $(CXXFLAGS) -c checkmatrix.cpp

test: checkmatrix compare

compare: compare.o
	$(CXX) $(CXXFLAGS) -o compare compare.o
compare.o: compare.cpp
	$(CXX) $(CXXFLAGS) -c compare.cpp
clean:
	rm -f *.o checkmatrix compare
