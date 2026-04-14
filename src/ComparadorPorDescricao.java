import java.util.Comparator;

public class ComparadorPorDescricao implements Comparator<Produto>{

    @Override
    public int compare(Produto esse, Produto outro) {
        return esse.compareTo(outro);
    }
    
}
