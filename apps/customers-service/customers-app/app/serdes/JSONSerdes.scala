package serdes;

import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.{Deserializer, Serializer}

class JsonPOJODeserializer[T >: Null](tClass: Class[T]) extends Deserializer[T] {

  private val mapper: ObjectMapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  private val PAYLOAD_FIELD: String = "payload"

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ???

  override def deserialize(topic: String, bytes: Array[Byte]): T = {

    if (bytes == null) {
      return null;
    }

    try {
      mapper.readValue(bytes, tClass)
    } catch {
      case _: Exception => {

        try {
          val payload = mapper.readValue[Map[String, Any]](new String(bytes), classOf[Map[String, Any]]).get(PAYLOAD_FIELD)
          return mapper.readValue(mapper.writeValueAsBytes(payload), tClass)
        } catch {
          case e: Exception => throw new SerializationException(e)
        }
      }
    }
  }

  override def close(): Unit = ???

}

class JsonPOJOSerializer[T <: Any](tClass: Class[T]) extends Serializer[T] {
  private val mapper: ObjectMapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ???

  override def serialize(topic: String, data: T): Array[Byte] = {
    if (data == null) {
      return Array.empty
    }

    try {
      return mapper.writeValueAsBytes(data)
    } catch {
      case e: Exception => throw new SerializationException("Error serializing JSON message", e)
    }
  }

  override def close(): Unit = ???
}
