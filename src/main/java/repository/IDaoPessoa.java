package repository;

import entidade.Pessoa;

public interface IDaoPessoa {

	Pessoa consultarUsuario(String login, String senha);
}
