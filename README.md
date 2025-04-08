# PUCFlix 1.0 - Sistema de Gerenciamento de Séries e Episódios

## 📽️ O que o trabalho faz?

O PUCFlix é um sistema de gerenciamento de catálogo de séries de streaming, permitindo o cadastro e organização de séries e seus respectivos episódios. O sistema foi desenvolvido seguindo o padrão de arquitetura MVC (Model-View-Controller) e utiliza estruturas de dados avançadas para indexação e busca eficiente das informações.

**Principais funcionalidades:**

### 🎬 Para Séries:
- Cadastrar novas séries com título, ano, sinopse e plataforma de streaming
- Buscar séries pelo nome
- Atualizar informações de séries existentes
- Excluir séries (com verificação de integridade referencial)
- Visualizar resumo de temporadas com contagem de episódios

### 📺 Para Episódios:
- Cadastrar novos episódios vinculados a séries específicas
- Buscar episódios por nome em todas as séries ou em uma série específica
- Atualizar informações de episódios existentes
- Excluir episódios
- Listar episódios organizados por temporada

### 📊 Relacionamentos e Estrutura:
- Implementação de relacionamento 1:N entre séries e episódios
- Uso de Árvore B+ para gerenciar o relacionamento entre séries e episódios
- Uso de Tabela Hash Extensível para acesso direto por ID
- Persistência dos dados em arquivos binários

O sistema garante a integridade referencial, impedindo a exclusão de séries que possuem episódios cadastrados, além de fornecer visualizações organizadas dos dados.

## 👨‍💻 Equipe

- Arthur Signorini
- Bernardo Vieira 
- Laura Persilva
- Otávio Augusto

## 📂 Estrutura do Projeto

### 🧩 Model (Camada de Dados)

**Serie.java**
- Representa a entidade Série com atributos como título, ano, sinopse e plataforma
- Implementa métodos de serialização e desserialização para persistência em arquivo

**Episodio.java**
- Representa a entidade Episódio com atributos como título, temporada, data de lançamento e duração
- Contém o atributo `serieId` como chave estrangeira para vinculação à série correspondente
- Implementa métodos de serialização e desserialização para persistência em arquivo

### 🎮 Controller (Camada de Controle)

**ControleSerie.java**
- Gerencia a lógica de negócio para séries
- Métodos principais: `menuSerie()`, `incluirSerie()`, `buscarSeriePorNome()`, `alterarSeriePorNome()`, `excluirSeriePorNome()`, `visualizarSerieComEpisodios()`
- Valida a integridade referencial antes de excluir séries

**ControleEpisodio.java**
- Gerencia a lógica de negócio para episódios
- Métodos principais: `menuEpisodio()`, `incluirEpisodio()`, `buscarEpisodioPorNome()`, `alterarEpisodioPorNome()`, `excluirEpisodioPorNome()`, `listarEpisodiosDaSerie()`
- Garante que episódios sejam vinculados apenas a séries existentes

**RelacionamentoSerieEpisodio.java**
- Gerencia o relacionamento 1:N entre séries e episódios
- Implementa a árvore B+ para indexação de relacionamentos
- Métodos principais: `adicionarRelacionamento()`, `removerRelacionamento()`, `getEpisodiosDaSerie()`, `serieTemEpisodios()`, `organizarEpisodiosPorTemporada()`
- Inclui mecanismos de recuperação caso a árvore B+ apresente problemas

### 👁️ View (Camada de Visualização)

**VisaoSerie.java**
- Interface com o usuário para operações relacionadas a séries
- Métodos principais: `leSerie()`, `mostraSerie()`, `mostraResultadoBuscaSeries()`, `mostraTodosEpisodiosPorTemporada()`

**VisaoEpisodio.java**
- Interface com o usuário para operações relacionadas a episódios
- Métodos principais: `leEpisodio()`, `mostraEpisodio()`, `mostraResultadoBuscaEpisodios()`, `mostraListaEpisodios()`

### 🛠️ Service (Camada de Serviço/Infraestrutura)

**Arquivo.java**
- Implementa o CRUD genérico para manipulação de arquivos
- Utiliza HashExtensivel como índice direto para acesso rápido por ID

**HashExtensivel.java**
- Implementa a estrutura de dados Tabela Hash Extensível
- Utilizada como índice direto para acesso por ID

**ArvoreBMais.java**
- Implementa a estrutura de dados Árvore B+
- Utilizada para gerenciar o relacionamento entre séries e episódios

**ParIDSerieEpisodio.java**
- Representa o par (idSerie, idEpisodio) para uso na Árvore B+
- Implementa a interface `RegistroArvoreBMais`

**ParIDEndereco.java**
- Representa o par (id, endereco) para uso na Tabela Hash Extensível
- Implementa a interface `RegistroHashExtensivel`

## 💻 Experiência de Desenvolvimento

O desenvolvimento do PUCFlix foi um desafio interessante que nos permitiu aplicar conceitos de estruturas de dados avançadas em um contexto prático. A seguir, relatamos nossa experiência:

### ✅ Requisitos Implementados
Conseguimos implementar todos os requisitos solicitados, incluindo:
- CRUD completo para séries e episódios
- Relacionamento 1:N entre séries e episódios usando Árvore B+
- Busca por nome em ambas as entidades
- Visualização dos episódios por temporada
- Verificação de integridade referencial

### 🚧 Desafios Enfrentados

O maior desafio foi a implementação e o uso correto da Árvore B+ para gerenciar o relacionamento entre séries e episódios. Enfrentamos alguns problemas de inconsistência na árvore, principalmente na inserção de novos relacionamentos. Para resolver esse problema, implementamos mecanismos de recuperação e verificação que garantem a consistência dos dados mesmo em caso de falha da árvore.

Outro desafio foi garantir a integridade referencial entre séries e episódios. Implementamos verificações em diversos pontos do sistema para garantir que episódios não sejam "órfãos" e que séries com episódios não possam ser excluídas.

A implementação do método `compareTo()` na classe `ParIDSerieEpisodio` também exigiu atenção especial, pois precisávamos garantir que a busca na árvore B+ funcionasse corretamente, especialmente para encontrar todos os episódios de uma série específica.

### 🏆 Resultados Alcançados

Conseguimos desenvolver um sistema funcional que atende a todos os requisitos especificados. O PUCFlix permite gerenciar séries e seus episódios de forma eficiente, com uma interface de linha de comando intuitiva e bem organizada.

A estrutura MVC adotada facilitou a manutenção e evolução do código, permitindo que cada componente do sistema tenha responsabilidades bem definidas.

Os mecanismos de recuperação implementados garantem a robustez do sistema, mesmo em casos de falha na estrutura de dados da árvore B+.

## ✓ Checklist de Requisitos

* As operações de inclusão, busca, alteração e exclusão de séries estão implementadas e funcionando corretamente? **Sim**
* As operações de inclusão, busca, alteração e exclusão de episódios, por série, estão implementadas e funcionando corretamente? **Sim**
* Essas operações usam a classe CRUD genérica para a construção do arquivo e as classes Tabela Hash Extensível e Árvore B+ como índices diretos e indiretos? **Sim**
* O atributo de ID de série, como chave estrangeira, foi criado na classe de episódios? **Sim**
* Há uma árvore B+ que registre o relacionamento 1:N entre episódios e séries? **Sim**
* Há uma visualização das séries que mostre os episódios por temporada? **Sim**
* A remoção de séries checa se há algum episódio vinculado a ela? **Sim**
* A inclusão da série em um episódio se limita às séries existentes? **Sim**
* O trabalho está funcionando corretamente? **Sim**
* O trabalho está completo? **Sim**
* O trabalho é original e não a cópia de um trabalho de outro grupo? **Sim**

## 📝 Conclusão

O desenvolvimento do PUCFlix foi uma experiência enriquecedora que nos permitiu aplicar conceitos teóricos de estruturas de dados em um contexto prático. Apesar dos desafios encontrados, principalmente relacionados à implementação da Árvore B+, conseguimos desenvolver um sistema funcional que atende a todos os requisitos especificados.

A organização do código em camadas (MVC) facilitou o desenvolvimento colaborativo e a manutenção do sistema. Além disso, a implementação de mecanismos de recuperação garantiu a robustez do sistema, mesmo em casos de falha na estrutura de dados.

Como melhorias futuras, poderíamos implementar uma interface gráfica para o sistema, além de adicionar novas funcionalidades como o cadastro de atores e a vinculação destes aos episódios.
