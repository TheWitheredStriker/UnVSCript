import org.eclipse.lsp4j.*
import org.eclipse.lsp4j.services.TextDocumentService
import java.util.HashMap
import java.util.List
import java.util.concurrent.CompletableFuture

/**
 * `TextDocumentService` that only supports `TextDocumentSyncKind.Full` updates.
 * Override members to add functionality.
 */

internal class FullTextDocumentService: TextDocumentService {

    var documents: HashMap<String, TextDocumentItem> = HashMap()

    @Override

    fun completion(position: TextDocumentPositionParams?): CompletableFuture<CompletionList>? {
        return null
    }

    @Override

    fun resolveCompletionItem(unresolved: CompletionItem?): CompletableFuture<CompletionItem>? {
        return null
    }

    @Override

    fun hover(position: TextDocumentPositionParams?): CompletableFuture<Hover>? {
        return null
    }

    @Override

    fun signatureHelp(position: TextDocumentPositionParams?): CompletableFuture<SignatureHelp>? {
        return null
    }

    @Override

    fun definition(position: TextDocumentPositionParams?): CompletableFuture<List<Location?>>? {
        return null
    }

    @Override

    fun references(params: ReferenceParams?): CompletableFuture<List<Location?>>? {
        return null
    }

    @Override

    fun documentHighlight(position: TextDocumentPositionParams?): CompletableFuture<List<DocumentHighlight?>>? {
        return null
    }

    @Override

    fun documentSymbol(params: DocumentSymbolParams?): CompletableFuture<List<SymbolInformation?>>? {
        return null
    }

    @Override

    fun codeAction(params: CodeActionParams?): CompletableFuture<List<Command?>>? {
        return null
    }

    @Override

    fun codeLens(params: CodeLensParams?): CompletableFuture<List<CodeLens?>>? {
        return null
    }

    @Override

    fun resolveCodeLens(unresolved: CodeLens?): CompletableFuture<CodeLens>? {
        return null
    }

    @Override

    fun formatting(params: DocumentFormattingParams?): CompletableFuture<List<TextEdit?>>? {
        return null
    }

    @Override

    fun rangeFormatting(params: DocumentRangeFormattingParams?): CompletableFuture<List<TextEdit?>>? {
        return null
    }

    @Override

    fun onTypeFormatting(params: DocumentOnTypeFormattingParams?): CompletableFuture<List<TextEdit?>>? {
        return null
    }

    @Override

    fun rename(params: RenameParams?): CompletableFuture<WorkspaceEdit>? {
        return null
    }

    @Override

    fun didOpen(params: DidOpenTextDocumentParams) {
        documents.put(params.getTextDocument().getUri(), params.getTextDocument())
    }

    @Override

    fun didChange(params: DidChangeTextDocumentParams) {
        val uri: String = params.getTextDocument().getUri()
        for (changeEvent in params.getContentChanges()) {

            // Will be full update because we specified that is all we support

            if (changeEvent.getRange() != null)
                throw UnsupportedOperationException("Range should be null for full document update.")

            if (changeEvent.getRangeLength() != null)
                throw UnsupportedOperationException("RangeLength should be null for full document update.")

            documents.get(uri).setText(changeEvent.getText())
        }
    }

    @Override

    fun didClose(params: DidCloseTextDocumentParams) {
        val uri: String = params.getTextDocument().getUri()
        documents.remove(uri)
    }

    @Override

    fun didSave(params: DidSaveTextDocumentParams?) {

    }
}