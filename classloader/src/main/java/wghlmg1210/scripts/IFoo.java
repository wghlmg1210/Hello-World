package wghlmg1210.scripts;

//import redis.clients.jedis.Jedis;

public interface IFoo {

    default Object run(String line) {
        return null;
    }

//    default Object run(Jedis jedis, String line) {
//        return null;
//    }

}
