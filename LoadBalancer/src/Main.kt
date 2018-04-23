import java.util.*

// host

class Host(private var load: Int, private val name: String) : IHost {

    override fun getLoad(): Int {
        return this.load
    }

    override fun getName(): String {
        return this.name
    }

    override fun handleRequest(): IHost {
        this.load--

        return this
    }
}

// balancing algorithms

class LowLoad(hosts: Array<IHost>, private val loadThreshold: Int) : AbstractLoadBalancingAlgorithm(hosts) {

    override fun nextHost(): IHost {
        var chosenHost = hosts[0]

        hosts.forEach {
            if (it.getLoad() <= this.loadThreshold)
                return it
            else if (it.getLoad() < chosenHost.getLoad())
                chosenHost = it
        }

        return chosenHost
    }
}

class RoundRobin(hosts: Array<IHost>) : AbstractLoadBalancingAlgorithm(hosts) {
    private var nextHostIndex: Int = 0

    override fun nextHost(): IHost {
        val host = this.hosts[this.nextHostIndex]

        this.nextHostIndex++
        this.nextHostIndex %= this.hosts.count()

        return host
    }
}

// abstract classes

abstract class AbstractLoadBalancingAlgorithm(hosts: Array<IHost>) : ILoadBalancingAlgorithm {
    protected val hosts: Array<IHost>

    private fun isArrayOfHosts(hosts: Array<IHost>): Boolean {
        if (hosts.isEmpty())
            return false

        return true
    }

    init {
        if (this.isArrayOfHosts(hosts))
            this.hosts = hosts
        else
            throw IllegalArgumentException("Given array doesn't include any hosts")
    }
}

// interfaces

interface IHost {
    fun getLoad(): Int

    fun getName(): String

    fun handleRequest(): IHost
}

interface ILoadBalancingAlgorithm {
    fun nextHost(): IHost
}

interface ILoadBalancer {
    fun handleRequest(): IHost
}

// load balancer class

class LoadBalancer(private val loadBalancingAlgorithm: ILoadBalancingAlgorithm) : ILoadBalancer {

    override fun handleRequest(): IHost {
        return this.loadBalancingAlgorithm.nextHost().handleRequest()
    }

    init {
        println("Testing using: ${loadBalancingAlgorithm.javaClass.name}")
    }
}


// main

fun test(loadBalancer: ILoadBalancer) {
    do {
        val host = loadBalancer.handleRequest()

        println("name: ${host.getName()}, load: ${host.getLoad()}")
    } while (host.getLoad() > 0)
}

fun main(args: Array<String>) {
    val hosts: Array<IHost> = arrayOf(
            Host(8, "host1"),
            Host(5, "host2"),
            Host(11, "host3")
    )

    hosts.forEach { println("name ${it.getName()}, load ${it.getLoad()}") }
    test(LoadBalancer(LowLoad(hosts, 7)))
    hosts.forEach { println("name ${it.getName()}, load ${it.getLoad()}") }

    // FIXME: https://stackoverflow.com/a/40574998/2065587
    val hosts2: Array<IHost> = arrayOf(
            Host(8, "host1"),
            Host(5, "host2"),
            Host(11, "host3")
    )

    test(LoadBalancer(RoundRobin(hosts2)))
}
