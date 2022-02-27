import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import org.eclipse.lsp4j.jsonrpc.Launcher
import org.eclipse.lsp4j.launch.LSPLauncher
import org.eclipse.lsp4j.services.LanguageClient

object App {

    fun main(args: Array<String?>) {
        System.out.println("Starting server!")
        val port = args[0]

        try {
            val socket = Socket("localhost", Integer.parseInt(port))
            val `in`: InputStream = socket.getInputStream()
            val out: OutputStream = socket.getOutputStream()
            val server = ExampleLanguageServer()
            val launcher: Launcher<LanguageClient> = LSPLauncher.createServerLauncher(server, `in`, out)
            val client: LanguageClient = launcher.getRemoteProxy()
            server.connect(client)
            launcher.startListening()
        }

        catch (e: IOException) {
            e.printStackTrace()
        }

        System.out.println("...aaaand we're done.")
    }
}