package rs.etf.sab.operations;

public class kl210041_PackageOperationsPair<A, B> implements PackageOperations.Pair{
    A first;
    B second;
    
    public kl210041_PackageOperationsPair(A a, B b){
        this.first = a;
        this.second = b;
    }
    
    @Override
    public Object getFirstParam() {
        return first;
    }

    @Override
    public Object getSecondParam() {
        return second;
    }
}
