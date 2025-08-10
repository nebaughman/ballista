package net.nyhm.ballista

import io.javalin.config.Key
import io.javalin.http.Context
import io.javalin.http.HttpResponseException
import net.nyhm.pick.Di
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.valueParameters

/**
 * @Deprecated use [isXHR]
 */
fun Context.isAjax() = isXHR()

/**
 * Report whether this context indicates it is an XHR request.
 *
 * Security note: This is based on headers sent by the client, so there is no guarantee of accuracy.
 */
fun Context.isXHR() = this.header("X-Requested-With") == "XMLHttpRequest"

/**
 * Resolve and process the endpoint with this request Context
 * (using the internally configured [Processor]).
 */
fun Context.process(endpoint: KFunction<*>) {
  appData(ProcessorKey).process(this, endpoint)
}

/**
 * A processor resolves an endpoint function to handle a request Context.
 */
interface Processor {
  fun process(ctx: Context, endpoint: KFunction<*>)
}

val ProcessorKey = Key<Processor>("processor")

fun interface ParamResolver {
  fun resolve(param: KParameter): Any?
}

/**
 * A [Processor] that uses a dependency injector to resolve dependencies.
 */
class DiProcessor(
  private val di: Di,
  private val resolver: ParamResolver = ParamResolver { null }
): Processor {

  override fun process(ctx: Context, endpoint: KFunction<*>) {

    val deps = mutableListOf<Any>()
    endpoint.valueParameters.forEach {
      val paramClass = it.type.classifier as KClass<*>
      if (paramClass == Context::class) deps.add(ctx)
      else if (isBodyParam(it)) deps.add(ctx.bodyAsClass(paramClass.java))
      else {
        val res = resolver.resolve(it)
        if (res != null) deps.add(res)
        else deps.add(di.get(paramClass))
      }
    }

    try {
      val result = endpoint.call(*deps.toTypedArray())
      if (result != null && result != Unit) ctx.json(result)
    } catch (e:InvocationTargetException) {
      if (e.cause is HttpResponseException) throw e.cause as HttpResponseException
      else throw e
    }
  }

  private fun isBodyParam(param: KParameter) = param.findAnnotation<Body>() != null
}

/**
 * Annotate an endpoint handler function parameter with @Body to have the
 * request Context body parsed and injected.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Body