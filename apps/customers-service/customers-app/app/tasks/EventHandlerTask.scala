package tasks

import events.PaymentEventHandlerTask
import com.google.inject.AbstractModule

class EventHandlerTaskModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[PaymentEventHandlerTask]).asEagerSingleton()
  }
}