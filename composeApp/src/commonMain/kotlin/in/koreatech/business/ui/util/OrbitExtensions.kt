package `in`.koreatech.business.ui.util

import kotlinx.coroutines.runBlocking
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitInternal
import org.orbitmvi.orbit.idling.withIdling
import org.orbitmvi.orbit.syntax.Syntax

@OptIn(OrbitInternal::class)
fun <STATE : Any, SIDE_EFFECT : Any> ContainerHost<STATE, SIDE_EFFECT>.blockingIntent(
    registerIdling: Boolean = false,
    transformer: suspend Syntax<STATE, SIDE_EFFECT>.() -> Unit
): Unit = runBlocking {
    container.inlineOrbit {
        withIdling(registerIdling) {
            Syntax(this).transformer()
        }
    }
}
