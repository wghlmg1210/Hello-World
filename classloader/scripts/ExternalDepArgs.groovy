import redis.clients.jedis.Jedis
import wghlmg1210.scripts.IFoo;

class Foo implements IFoo {
    public Object run(String line) {
        Jedis jedis = new Jedis(line)
        println(jedis.getClass().getClassLoader())
        println(jedis)

        return 2 + 2 > 1
    }
}