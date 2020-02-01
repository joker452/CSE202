CXX = g++
CXXFLAGS = -Wall

test: checkmatrix compare

compare: compare.o
	$(CXX) $(CXXFLAGS) -o compare compare.o
compare.o: compare.cpp
	$(CXX) $(CXXFLAGS) -c compare.cpp
checkmatrix: checkmatrix.o
	$(CXX) $(CXXFLAGS) -o checkmatrix checkmatrix.o
checkmatrix.o: checkmatrix.cpp
	$(CXX) $(CXXFLAGS) -c checkmatrix.cpp
clean:
	rm -f *.o checkmatrix compare
