# 📊 Colepp — Ferramenta Multiplataforma para Coleta de Dados de Dispositivos Vestíveis

## 🧩 Descrição

O **Colepp** é uma ferramenta **open-source** para coleta e sincronização de dados fisiológicos e de movimento provenientes de dispositivos vestíveis, como smartwatches e cintas de monitoramento cardíaco.  

O sistema é composto por **dois módulos principais**:
- **Mobile (Android)** — centraliza o controle e armazenamento dos dados, gerencia conexões e exporta as coletas.
- **Wear (Wear OS)** — roda no smartwatch, coleta dados dos sensores embarcados e envia ao app mobile.

O foco é permitir **coleta estruturada e sincronizada**, facilitando a criação de datasets para pesquisas nas áreas de monitoramento de saúde, detecção de atividades humanas (HAR) e desenvolvimento de algoritmos de estimativa de frequência cardíaca.

---

## 🗂️ Sumário

- [📊 Colepp — Ferramenta Multiplataforma para Coleta de Dados de Dispositivos Vestíveis](#-colepp--ferramenta-multiplataforma-para-coleta-de-dados-de-dispositivos-vestíveis)
  - [🧩 Descrição](#-descrição)
  - [📱 Funcionalidades](#-funcionalidades)
    - [Módulo Mobile (Android)](#módulo-mobile-android)
    - [Módulo Wear (Wear OS)](#módulo-wear-wear-os)
  - [⚙️ Instalação e Execução](#️-instalação-e-execução)
  - [📱 Guia de Uso](#-guia-de-uso)
    - [Iniciar uma nova captura](#iniciar-uma-nova-captura)
    - [Conectar dispositivos](#conectar-dispositivos)
    - [Excluir uma captura](#excluir-uma-captura)
    - [Editar uma captura](#editar-uma-captura)
    - [Baixar uma captura em formato CSV](#baixar-uma-captura-em-formato-csv)
    - [Compartilhar uma captura em formato CSV](#compartilhar-uma-captura-em-formato-csv)
  - [🤝 Contribuição](#-contribuição)
  - [📄 Licença](#-licença)

---

## 📱 Funcionalidades

### Módulo Mobile (Android)
- **Conexão com dispositivos**: pareamento com a cinta Polar H10 (via BLE) e smartwatch Wear OS.
- **Coleta simultânea**:
  - Polar H10: Acelerômetro e frequência cardíaca via ECG.
  - Wear OS: Acelerômetro, giroscópio e frequência cardíaca via PPG.
- **Sincronização temporal precisa** entre dados de ambos os dispositivos, com protocolo de offset temporal usando a API `MessageClient`.
- **Armazenamento local** em banco de dados SQLite.
- **Exportação para CSV** sob demanda.
- **Interface intuitiva**:
  - Tela inicial para nova coleta ou histórico.
  - Instruções de uso.
  - Detalhes de sessão com metadados e gráfico de FC.
  - Coleta em tempo real com status de conexão e dados atualizados.

### Módulo Wear (Wear OS)
- **Tela de espera**: indica status de prontidão e conexão.
- **Tela de coleta ativa**: exibe FC estimada via PPG e indicador visual de coleta.
- **Envio contínuo de dados** para o app mobile.
- **Compatibilidade**: Wear OS 3 ou superior.

---

## ⚙️ Instalação e Execução

1. **Clonar o repositório**:
   ```bash
   git clone https://github.com/life-ufes/Wearable-Health-Data-Sync.git
   ```
2. **Abrir no Android Studio**:

- Vá em `File > Open` e selecione a pasta do projeto.

3. **Configurar dispositivos**:

- Siga o guia oficial para parear smartwatch e smartphone: https://developer.android.com/studio/run/device

4. **Executar módulos**:

- Selecione a configuração mobile e rode no smartphone.

- Selecione a configuração wear e rode no smartwatch.

---

## 📱 Guia de Uso

#### Iniciar uma nova captura

1. Na **Home**, clique em **<code><img src="assets/ic_add.svg" width="14" height="14" style="vertical-align:middle;"> Nova captura</code>** no menu de navegação inferior.
2. Você será redirecionado para a tela de **Nova captura**:
   - Insira um **nome** com mais de 3 caracteres (a descrição é opcional).
3. Clique em **Criar** para ir até a tela de **Iniciar captura** [(guia de conexão)](#conectar-dispositivos).
4. Certifique-se de que **os dois dispositivos estejam conectados**.
5. Clique em **Iniciar**:
   - Uma contagem regressiva de 3 segundos será exibida antes do início da captura.
6. A captura será salva **localmente** até que você:
   - Clique em **Finalizar**  
   **ou**
   - Algum dispositivo seja desconectado.
7. Para finalizar, clique em **Voltar** e você retornará à **Home**, onde verá a lista de todas as capturas salvas.

#### Conectar dispositivos

1. Para que o aplicativo consiga fazer a detecção e conecção automatica com o Polar H10 e o smartwatch é necessario que os dispositivos já estejam pareados com o aparelho.
2. Com o bluetooth ligado e os dispositivos pareados com o aparelho é só clicar em cada card que vai ser iniciado a conexão automática.
3. Espere alguns segundos para eles estarem conectados, vai ter um feedback quando estiver tudo certo, e o botão vai ficar habilitado.
4. No caso do smartwatch, ao clicar no card, o aplicativo será iniciado automaticamente no dispositivo. Caso isso não aconteça, ou se preferir, você pode abri-lo manualmente tocando no ícone do app no próprio smartwatch.

#### Excluir uma captura

1. Na **Home**, clique no ícone <img src="assets/ic_delete.svg" width="16" height="16" style="vertical-align:middle;"> no card da captura que deseja excluir.
2. Será exibida uma caixa de confirmação, pois **não será possível recuperar** o dado após a exclusão.
3. Para confirmar, clique em **Excluir**.
4. Para cancelar a operação, clique em **Cancelar**.

#### Editar uma captura

1. Na **Home**, clique no ícone <img src="assets/ic_edit.svg" width="16" height="16" style="vertical-align:middle;"> no card da captura que deseja editar.
2. Você será redirecionado para a tela **Editar captura**, onde poderá alterar o nome e a descrição.
3. Para confirmar as alterações, clique em **Salvar**.
4. Para desistir, clique em **Voltar** no canto superior esquerdo ou utilize a barra de navegação.

#### Baixar uma captura em formato CSV

1. Clique no **card** da captura desejada.
2. Na tela de **detalhes da captura**, clique no ícone <img src="assets/ic_download.svg" width="16" height="16" style="vertical-align:middle;"> no canto superior direito.
3. Aguarde a caixa de diálogo com **carregamento** enquanto o arquivo é gerado.
4. Se der certo:
   - Será exibida uma mensagem de **sucesso** com o local onde o arquivo foi salvo (normalmente na pasta padrão de *Downloads* do dispositivo).
5. Se der erro:
   - Uma mensagem informando o problema será exibida.


#### Compartilhar uma captura em formato CSV

1. Clique no **card** da captura desejada.
2. Na tela de **detalhes da captura**, clique no ícone <img src="assets/ic_share.svg" width="16" height="16" style="vertical-align:middle;"> no canto superior direito.
3. Aguarde a caixa de diálogo com **carregamento** enquanto o arquivo é gerado.
4. Se der certo:
   - Um **Menu de Compartilhamento** aparecerá, listando os aplicativos possíveis para envio.
5. Se der erro:
   - Uma mensagem informando o problema será exibida.

---

## 🤝 Contribuição
Contribuições são bem-vindas!
Para colaborar:

1. **Fork** o repositório.

2. Crie uma **branch** com sua feature/ajuste:

```bash
git checkout -b minha-feature
```

3. **Commit** suas alterações:

```bash
git commit -m "Adiciona nova funcionalidade X"
```

4. **Push** para a branch:

```bash
git push origin minha-feature
```

5. Abra um **Pull Request**.

---

## 📄 Licença
Este projeto está licenciado sob a Licença MIT — consulte o arquivo [LICENSE](LICENSE) para mais detalhes.