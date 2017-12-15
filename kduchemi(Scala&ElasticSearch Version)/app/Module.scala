import com.google.inject.AbstractModule
import com.sksamuel.elastic4s.{ElasticsearchClientUri, TcpClient}
import org.elasticsearch.common.settings.Settings
import services.{ElaTest, Products}

/**
  * This class is a Guice module that tells Guice how to bind several
  * different types. This Guice module is created when the Play
  * application starts.
  *
  * Play will automatically use any class called `Module` that is in
  * the root package. You can create modules in other locations by
  * adding `play.modules.enabled` settings to the `application.conf`
  * configuration file.
  */
class Module extends AbstractModule {

  override def configure() = {
    val settings = Settings.builder().put("cluster.name", "elasticsearch").build()
    val clientTCP = TcpClient.transport(settings, ElasticsearchClientUri("elasticsearch://localhost:9300"))
    bind(classOf[Products]).toInstance(new Products(clientTCP))
    bind(classOf[ElaTest]).toInstance(new ElaTest(clientTCP))
  }
}