package be.bagofwords.cache;

import be.bagofwords.UnitTestContextLoader;
import be.bagofwords.application.memory.MemoryManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Random;

/**
 * Created by Koen Deschacht (koendeschacht@gmail.com) on 9/25/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = UnitTestContextLoader.class)
public class TestReadCache {

    @Autowired
    private MemoryManager memoryManager;
    @Autowired
    private CachesManager cachesManager;

    @Test
    public void testRemoval() throws Exception {
        final int numOfValues = 1000;
        Random random = new Random();
        char[] randomString = createRandomString(random);
        ReadCache<String> firstReadCache = cachesManager.createNewCache("cache1", String.class);
        for (int i = 0; i < numOfValues; i++) {
            firstReadCache.put(random.nextLong(), new String(randomString));
        }
        firstReadCache.updateCachedObjects();
        Assert.assertTrue(firstReadCache.size() > 0);
        //Eventually all values should be removed
        ReadCache<String> otherReadCache = cachesManager.createNewCache("cache2", String.class);
        long maxTimeToTry = 5 * 60 * 1000; //usually this should take less then 5 minutes
        long start = System.currentTimeMillis();
        while (start + maxTimeToTry >= System.currentTimeMillis() && firstReadCache.size() > 0) {
            memoryManager.waitForSufficientMemory();
            otherReadCache.put(random.nextLong(), new String(randomString));
        }
        Assert.assertEquals(0, firstReadCache.size());
        cachesManager.createNewCache("unused_cache", Long.class); //just to make sure that the caches manager is not garbage collected before the end of the test
    }

    private char[] createRandomString(Random random) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextLong());
        }
        return sb.toString().toCharArray();
    }

}
