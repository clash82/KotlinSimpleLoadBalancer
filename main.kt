// host

class Host: IHost {
    private var load: Int

    private val name: String

    constructor(load: Int, name: String) {
        this.load = load
        this.name = name
    }

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

class LowLoad: AbstractLoadBalancingAlgorithm {
    private var loadThreshold: Int

    constructor(hosts: Array<IHost>, loadThreshold: Int): super(hosts) {
        this.loadThreshold = loadThreshold
    }

    override fun nextHost(): IHost {
        var chosenHost = this.hosts[0]

        this.hosts.forEach {
            if (it.getLoad() <= this.loadThreshold)
                return it
            else if (it.getLoad() < chosenHost.getLoad())
                chosenHost = it
        }

        return chosenHost
    }
}

class RoundRobin: AbstractLoadBalancingAlgorithm {
    private var nextHostIndex: Int = 0

    constructor(hosts: Array<IHost>): super(hosts)

    override fun nextHost(): IHost {
        var host = this.hosts[this.nextHostIndex]

        this.nextHostIndex++
        this.nextHostIndex %= this.hosts.count()

        return host
    }
}

// abstract classes

abstract class AbstractLoadBalancingAlgorithm: ILoadBalancingAlgorithm {
    protected val hosts: Array<IHost>

    constructor(hosts: Array<IHost>) {
        if (this.isArrayOfHosts(hosts))
            this.hosts = hosts
        else
            throw IllegalArgumentException("Given array doesn't include any hosts")
    }

    fun isArrayOfHosts(hosts: Array<IHost>): Boolean {
        if (hosts.isEmpty())
            return false

        return true
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

class LoadBalancer: ILoadBalancer {
    protected val loadBalancingAlgorithm: ILoadBalancingAlgorithm

    constructor(loadBalancingAlgorithm: ILoadBalancingAlgorithm) {
        println("Testing using: ${loadBalancingAlgorithm.javaClass.name}")

        this.loadBalancingAlgorithm = loadBalancingAlgorithm
    }

    override fun handleRequest(): IHost {
        return this.loadBalancingAlgorithm.nextHost().handleRequest()
    }
}


// main

fun test(loadBalancer: ILoadBalancer) {
    do {
        var host = loadBalancer.handleRequest()

        println("name: ${host.getName()}, load: ${host.getLoad()}")
    } while (host.getLoad() > 0)
}

fun main(args: Array<String>) {
    val hosts: Array<IHost> = arrayOf(
            Host(8, "host1"),
            Host(5, "host2"),
            Host(11 ,"host3")
    )

    test(LoadBalancer(LowLoad(hosts, 7)))

    // TODO: how to pass array by value and not by reference?
    val hosts2: Array<IHost> = arrayOf(
            Host(8, "host1"),
            Host(5, "host2"),
            Host(11 ,"host3")
    )

    test(LoadBalancer(RoundRobin(hosts2)))
}
