# PUCFlix 1.0 - Sistema de Gerenciamento de Séries

O PUCFlix é um sistema completo para gerenciamento de séries e episódios, desenvolvido em Java seguindo o padrão MVC e utilizando estruturas de dados avançadas

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
