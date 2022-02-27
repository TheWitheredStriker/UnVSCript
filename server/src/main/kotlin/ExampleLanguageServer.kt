import org.eclipse.lsp4j.InitializeParams
import org.eclipse.lsp4j.InitializeResult
import org.eclipse.lsp4j.*
import org.eclipse.lsp4j.ServerCapabilities
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageClientAware
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.services.WorkspaceService
import java.util.ArrayList
import java.util.List
import java.util.Map
import java.util.concurrent.CompletableFuture

internal class ExampleLanguageServer: LanguageServer, LanguageClientAware {
    private var client: LanguageClient? = null

    @SuppressWarnings("unused")

    private var workspaceRoot: String? = null

    @Override

    fun initialize(params: InitializeParams): CompletableFuture<InitializeResult> {
        System.out.println("initialize")
        workspaceRoot = params.getRootPath()
        val capabilities = ServerCapabilities()
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full)
        capabilities.setCodeActionProvider(false)
        capabilities.setCompletionProvider(CompletionOptions(true, null))
        return CompletableFuture.completedFuture(InitializeResult(capabilities))
    }

    @Override

    fun shutdown(): CompletableFuture<Object> {
        System.out.println("shutdown")
        return CompletableFuture.completedFuture(null)
    }

    @Override

    fun exit() {
        System.out.println("exit")
    }

    private val fullTextDocumentService: FullTextDocumentService = object: FullTextDocumentService() {

        @Override

        fun completion(textDocumentPosition: TextDocumentPositionParams?): CompletableFuture<CompletionList> {
            System.out.println("completion")
            val typescriptCompletionItem = CompletionItem()
            typescriptCompletionItem.setLabel("TypeScript")
            typescriptCompletionItem.setKind(CompletionItemKind.Text)
            typescriptCompletionItem.setData(1.0)
            val javascriptCompletionItem = CompletionItem()
            javascriptCompletionItem.setLabel("JavaScript")
            javascriptCompletionItem.setKind(CompletionItemKind.Text)
            javascriptCompletionItem.setData(2.0)
            val completions: List<CompletionItem> = ArrayList()
            completions.add(typescriptCompletionItem)
            completions.add(javascriptCompletionItem)
            return CompletableFuture.completedFuture(CompletionList(false, completions))
        }

        @Override

        fun resolveCompletionItem(item: CompletionItem): CompletableFuture<CompletionItem> {
            System.out.println("resolveCompletionItem")
            if (item.getData().equals(1.0)) {
                item.setDetail("TypeScript details")
                item.setDocumentation("TypeScript documentation")
            } else if (item.getData().equals(2.0)) {
                item.setDetail("JavaScript details")
                item.setDocumentation("JavaScript documentation")
            }
            return CompletableFuture.completedFuture(item)
        }

        @Override

        fun didChange(params: DidChangeTextDocumentParams) {
            System.out.println("didChange")
            super.didChange(params)
            val document: TextDocumentItem = this.documents.get(params.getTextDocument().getUri())
            validateDocument(document)
        }

    }

    @get: Override

    val textDocumentService: TextDocumentService
        get() {
            System.out.println("getTextDocumentService")
            return fullTextDocumentService
        }

    private fun validateDocument(document: TextDocumentItem) {
        val diagnostics: List<Diagnostic> = ArrayList()
        val lines: Array<String> = document.getText().split("\\r?\\n")
        var problems = 0
        var i = 0
        while (i < lines.size && problems < maxNumberOfProblems) {
            val line = lines[i]
            val index: Int = line.indexOf("typescript")
            if (index >= 0) {
                problems++
                val diagnostic = Diagnostic()
                diagnostic.setSeverity(DiagnosticSeverity.Warning)
                diagnostic.setRange(Range(Position(i, index), Position(i, index + 10)))
                diagnostic.setMessage(String.format("%s should be spelled TypeScript", line.substring(index, index + 10)))
                diagnostic.setSource("ex")
                diagnostics.add(diagnostic)
            }
            i++
        }
        client.publishDiagnostics(PublishDiagnosticsParams(document.getUri(), diagnostics))
    }

    private var maxNumberOfProblems = 100

    @get: Override

    val workspaceService: WorkspaceService
        get() {
            System.out.println("getWorkspaceService")
            return object: WorkspaceService() {

                @Override

                fun symbol(params: WorkspaceSymbolParams?): CompletableFuture<List<SymbolInformation?>>? {
                    System.out.println("symbol")
                    return null
                }

                @Override

                fun didChangeConfiguration(params: DidChangeConfigurationParams) {
                    System.out.println("didChangeConfiguration")
                    maxNumberOfProblems = (params.getSettings()["unvscriptLS"].getOrDefault("maxNumberOfProblems", 100.0) as Double).intValue()
                    fullTextDocumentService.documents.values().forEach { d -> validateDocument(d) }
                }

                @Override

                fun didChangeWatchedFiles(params: DidChangeWatchedFilesParams?) {
                    System.out.println("didChangeWatchedFiles")
                    client.logMessage(MessageParams(MessageType.Log, "We received an file change event"))
                }
            }
        }

    @Override

    fun connect(client: LanguageClient?) {
        System.out.println("connect")
        this.client = client
    }
}