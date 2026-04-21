import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Pedido implements Comparable<Pedido>{

	private static int ultimoID = 1;

	private int idPedido;

	/** Quantidade máxima de itens diferentes de um pedido */
	private static final int MAX_ITENS = 10;

	/** Porcentagem de desconto para pagamentos à vista */
	private static final double DESCONTO_PG_A_VISTA = 0.15;

	/** Vetor para armazenar os itens do pedido */
	private ItemDePedido[] itens;

	/** Data de criação do pedido */
	private LocalDate dataPedido;

	/** Indica a quantidade de itens no pedido até o momento */
	private int quantItens = 0;

	/** Indica a forma de pagamento do pedido sendo: 1, pagamento à vista; 2, pagamento parcelado */
	private int formaDePagamento;

	/** Construtor do pedido.
	 *  Cria o vetor de itens do pedido,
	 *  armazena a data, o código identificador e a forma de pagamento informados para o pedido.
	 */
	public Pedido(LocalDate dataPedido, int formaDePagamento) {

		idPedido = ultimoID++;
		itens = new ItemDePedido[MAX_ITENS];
		quantItens = 0;
		this.dataPedido = dataPedido;
		this.formaDePagamento = formaDePagamento;
	}

	/**
     * Inclui um produto neste pedido com quantidade 1, congelando o preço atual do produto.
     * @param novo O produto a ser incluído no pedido
     * @return true/false indicando se a inclusão foi realizada com sucesso.
     */
	public boolean incluirProduto(Produto novo) {
		return incluirProduto(novo, 1);
	}

	/**
     * Inclui um produto neste pedido com a quantidade informada, congelando o preço atual do produto.
     * @param novo      O produto a ser incluído no pedido
     * @param quantidade Quantidade de unidades deste produto
     * @return true/false indicando se a inclusão foi realizada com sucesso.
     */
	public boolean incluirProduto(Produto novo, int quantidade) {

		if (quantItens < MAX_ITENS) {
			itens[quantItens++] = new ItemDePedido(novo, quantidade);
			return true;
		}
		return false;
	}

	/**
     * Calcula e retorna o valor final do pedido com base nos preços congelados dos itens.
     * Caso a forma de pagamento seja à vista, aplica o desconto correspondente.
     * @return Valor final do pedido (double)
     */
	public double valorFinal() {

		double valorPedido = 0;
		BigDecimal valorPedidoBD;

		for(int i = 0; i < itens.length; i++){
			ItemDePedido item = itens[i];
			valorPedido += item.getPrecoVenda() * item.getQuantidade();
		}

		if (formaDePagamento == 1) {
			double desconto = valorPedido * DESCONTO_PG_A_VISTA;
			valorPedido -= desconto;
		}

		return valorPedido;
	}

	/**
     * Calcula e retorna a soma dos valores de venda ATUAIS de catálogo de todos os itens,
     * sem aplicar nenhum desconto por forma de pagamento.
     * Permite comparar o que o cliente pagou (preço congelado) com o preço corrente do catálogo.
     * @return Valor total ao preço de catálogo atual (double).
     */
    public double valorCatalogoAtual() {
        double soma = 0;
        for(ItemDePedido i : itens){
			soma += i.getPrecoVenda() * i.getQuantidade();
		}
        return soma;
    }

    /**
     * Calcula o índice de economia do pedido: diferença entre o valor total ao preço de catálogo
     * atual e o valor efetivamente pago (com preços congelados e desconto de pagamento).
     * @return Índice de economia (double).
     */
    public double indiceEconomia() {
        //Sua lógica de cálculo do índice de economia aqui

		double diferenca = valorFinal() - valorCatalogoAtual();

		return diferenca;
    }

	/**
     * Representação em String do pedido (recibo).
     * Exibe cabeçalho, itens com preços congelados, forma de pagamento e valor final.
     */
	@Override
	public String toString() {

		StringBuilder stringPedido = new StringBuilder();

		stringPedido.append(String.format("Número do pedido: %02d\n", idPedido));

		DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		stringPedido.append("Data do pedido: " + formatoData.format(dataPedido) + "\n");

		stringPedido.append("Pedido com " + quantItens + " produto(s).\n");
		stringPedido.append("Produtos no pedido:\n");
		for (int i = 0; i < quantItens; i++) {
			stringPedido.append(itens[i].toString() + "\n");
		}

		stringPedido.append("Pedido pago ");
		if (formaDePagamento == 1) {
			stringPedido.append("à vista. Percentual de desconto: " + String.format("%.2f", DESCONTO_PG_A_VISTA * 100) + "%\n");
		} else {
			stringPedido.append("parcelado.\n");
		}

		stringPedido.append("Valor total do pedido: R$ " + String.format("%.2f", valorFinal()));

		return stringPedido.toString();
	}

    /**
     * Comparação padrão do pedido: identificador.
     */
    @Override
    public int compareTo(Pedido outro) {
    	if (this.idPedido == outro.idPedido) {
    		return 0;
    	} else if (this.idPedido < outro.idPedido) {
    		return -1;
    	} else {
    		return 1;
    	}
    }

	public int comparePrecoFinal(Pedido outro){

		int maior;

		if(this.valorFinal() > outro.valorFinal()){
			maior = 1;
		}
		else if(this.valorFinal() < outro.valorFinal()){
			maior = -1;
		}
		else{
			maior = desempatePorVolumeDeItens(outro);
		}

		return maior;
	}

    private int desempatePorVolumeDeItens(Pedido outro) {
		int volumeItensDesse = getTotalItens();
		int volumeItensOutro = outro.getTotalItens();

		int maior;

		if(volumeItensDesse > volumeItensOutro){
			maior = 1;
		}
		else if(volumeItensDesse < volumeItensOutro){
			maior = -1;
		}
		else{
			maior = desempatePorIdPrimeiroItem(outro);
		}

		return maior;
	}

	private int desempatePorIdPrimeiroItem(Pedido outro) {
		int maior;
		int idPrimeiroItem = getIdPrimeiroProduto();
		int idPrimeiroItemOutro = outro.getIdPrimeiroProduto();

		if(idPrimeiroItem > idPrimeiroItemOutro){
			maior = 1;
		}
		else{
			maior = -1;
		}

		return maior;
	}

	public LocalDate getDataPedido() {
    	return dataPedido;
    }

    public int getIdPedido() {
    	return idPedido;
    }

    /** Retorna a quantidade de itens (entradas distintas) no pedido. */
    public int getQuantosProdutos() {
    	return quantItens;
    }

    /**
     * Retorna o total de unidades físicas encomendadas, somando as quantidades de todos os itens.
     * @return Total de unidades físicas do pedido.
     */
    public int getTotalItens() {
        int total = 0;
        for (int i = 0; i < quantItens; i++) {
            total += itens[i].getQuantidade();
        }
        return total;
    }

    /**
     * Retorna o código identificador do primeiro produto do pedido.
     * @return ID do primeiro produto, ou 0 se o pedido estiver vazio.
     */
    public int getIdPrimeiroProduto() {
        if (quantItens > 0 && itens[0] != null) {
            return itens[0].getProduto().hashCode();
        }
        return 0;
    }



	public int compareFormaDePagamento(Pedido outro) {

		int maior;

		if(this.formaDePagamento() > outro.formaDePagamento){
			maior = 1;
		}
		else if (this.formaDePagamento() < outro.formaDePagamento){
			maior = -1;
		}
		else{
			maior = comparePrecoFinal(outro);
		}

		return maior;

	}

	private int formaDePagamento() {
		return formaDePagamento;
	}

	public int compareTicketMedio(Pedido outro) {
		int maior;

		double ticketMedioEsse = calcularTicketMedio();
		double ticketMedioOutro = outro.calcularTicketMedio();

		if(ticketMedioEsse > ticketMedioOutro){
			maior = 1;
		}
		else if (ticketMedioEsse < ticketMedioOutro){
			maior = -1;
		}
		else{
			maior = compareCodigoIdentificador(outro);
		}

		return maior;
	}

	private int compareCodigoIdentificador(Pedido outro) {
		if(this.getIdPrimeiroProduto() > outro.getIdPrimeiroProduto()){
			return 1;
		}
		else{
			return -1;
		}
	}

	private double calcularTicketMedio(){

		double valorTicket = 0;

		for(ItemDePedido i : itens){
			valorTicket += i.getPrecoVenda() * i.getQuantidade();
		}

		return valorTicket = valorTicket / itens.length;
	}
}
