# PUCFlix 1.0 - Sistema de Gerenciamento de Séries

**🤖 Descrição do Trabalho**

O trabalho implementa um sistema de gerenciamento de catálogo de streaming chamado "PUCFlix", que permite organizar séries e seus respectivos episódios. O sistema possibilita o gerenciamento completo de entidades (séries e episódios) com as operações CRUD (Create, Read, Update, Delete) utilizando arquivos para persistência de dados e estruturas de indexação avançadas.

O PUCFlix armazena as séries e episódios em arquivos binários, utilizando uma tabela hash extensível para acesso direto (pelo ID) e uma árvore B+ para gerenciar os relacionamentos entre séries e episódios. A implementação segue o padrão MVC (Model-View-Controller), com clara separação entre as camadas de modelo de dados, visualização e controle.

O sistema permite:
- Cadastrar, buscar, alterar e excluir séries
- Cadastrar, buscar, alterar e excluir episódios vinculados a séries
- Visualizar estatísticas sobre as séries e temporadas
- Listar episódios por temporada
- Visualizar a estrutura da árvore B+ de relacionamentos

O trabalho implementa diversos mecanismos de segurança e recuperação, como verificações de integridade referencial (impedir a exclusão de séries com episódios), validação de entradas, e mecanismos de recuperação para problemas com a árvore B+.


Imagine que você quer catalogar todas as séries da Netflix. Nosso sistema:

1️⃣ Cadastra Stranger Things (ano, sinopse, etc.)

2️⃣ Adiciona episódios (Temp 1 Ep 1: "The Vanishing of Will Byers")

3️⃣ Busca rapidamente (ex: "Mostrar todos episódios da Temp 2")

4️⃣ Não deixa excluir se houver episódios vinculados.

<hr></hr>

✨ **Funcionalidades**

Para Séries:

* Cadastro completo com nome, ano, sinopse e plataforma

* Operações CRUD (Criar, Ler, Atualizar, Deletar)

* Validação de integridade referencial

Para Episódios:
  
* Vinculação automática com séries

* Organização por temporadas

* Gerenciamento completo de episódios

<hr></hr>

📂 **Estrutura do Projeto**

**Camada Model**

* Serie.java - Entidade principal com serialização
* Episodio.java	- Entidade com relacionamento a séries
  
**Camada Controller**

* ControleSerie.java - Lógica de negócios para séries
* ControleEpisodio.java - Gestão de episódios
* RelacionamentoSerieEpisodio.java - Gerencia Árvore B+ de relacionamentos

**Camada View**

* VisaoSerie.java - Interface de usuário para séries
* VisaoEpisodio.java - Interface para episódios

**Infraestrutura**

* ArvoreBMais.java - Árvore B+ para índices
* HashExtensivel.java - Tabela hash para acesso rápido
* Arquivo.java - Manipulação de arquivos

<hr></hr>

**👨‍💻 Equipe de Desenvolvimento**

Arthur Signorini

Bernardo Vieira

Laura Persilva

Otávio Augusto
