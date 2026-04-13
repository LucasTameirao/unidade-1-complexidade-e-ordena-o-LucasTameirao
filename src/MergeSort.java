public class MergeSort<T extends Comparable<T>> implements IOrdenador<T>{

    private void mergeSort(int[] vet, int direita, int esquerda){
        if (esquerda < direita) {
            int meio  = (esquerda + direita) / 2;
            mergeSort(vet, esquerda, meio);
            mergeSort(vet, meio + 1, direita);
            intercalar(vet, esquerda, meio, direita);
        }
    }

    private void intercalar(int[] array, int esquerda, int meio, int direita){
        int n1, n2, i, j, k;


        // definir o tamanho dos dois subarrays

        n1 = meio - esquerda + 1;
        n2 = direita - meio;

        int[] a1 = new int[n1];
        int [] a2 = new int[n2];

        //inicializar o primeira subarray

        for(i = 0; i < n1; i++){
            a1[i] = array[esquerda + i];
        }

        //inicializar o segundo subarray

        for(j = 0; j < n2; j++){
            a2[j] = array[meio + j + 1];
        }

        // intercalação

        for(i = j = 0, k = esquerda; (i < n1 && j < n2); k++){
            if(a1[i] <= a2[j]){
                array[k] = a1[i++];
            }
            else{
                array[k] = a2[j++];
            }
        }

        if(i == n1){
            for(; k <= direita; k++){
                array[k] = a2[j++];
            }
        }
        else{
            for(; k <= direita; k++){
                array[k] = a1[i++];
            }
        }

    }


    @Override
    public T[] ordenar(T[] dados) {
        
        int esquerda = 0;
        int direita = dados.length;
        mergeSort(dados, direita, esquerda);
        return dados;
    }



    @Override
    public long getComparacoes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getComparacoes'");
    }

    @Override
    public long getMovimentacoes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMovimentacoes'");
    }

    @Override
    public double getTempoOrdenacao() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTempoOrdenacao'");
    }
    
}
