package repository;

import java.util.List;

import entidade.Lancamento;

public interface IDaoLancamento {

	List<Lancamento> consultar(Long codUser);
}
