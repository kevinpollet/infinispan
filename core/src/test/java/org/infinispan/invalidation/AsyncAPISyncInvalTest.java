package org.infinispan.invalidation;

import org.infinispan.Cache;
import org.infinispan.commands.write.WriteCommand;
import org.infinispan.config.Configuration;
import org.infinispan.context.Flag;
import org.infinispan.test.MultipleCacheManagersTest;
import org.infinispan.test.data.Key;
import org.infinispan.transaction.lookup.DummyTransactionManagerLookup;
import org.infinispan.util.Util;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Test(groups = "functional", testName = "invalidation.AsyncAPISyncInvalTest")
public class AsyncAPISyncInvalTest extends MultipleCacheManagersTest {

   Cache<Key, String> c1, c2;

   @SuppressWarnings("unchecked")
   protected void createCacheManagers() throws Throwable {
      Configuration c =
            getDefaultClusteredConfig(sync() ? Configuration.CacheMode.INVALIDATION_SYNC : Configuration.CacheMode.INVALIDATION_ASYNC);
      c.setTransactionManagerLookupClass(DummyTransactionManagerLookup.class.getName());
      List<Cache<Key, String>> l = createClusteredCaches(2, getClass().getSimpleName(), c);
      c1 = l.get(0);
      c2 = l.get(1);
   }

   protected boolean sync() {
      return true;
   }

   protected void asyncWait(Class<? extends WriteCommand>... cmds) {
   }

   protected void resetListeners() {
   }

   private void assertInvalidated(Key k, String value) {
      assert Util.safeEquals(c1.get(k), value);
      assert !c2.containsKey(k);
   }

   private void initC2(Key k) {
      c2.getAdvancedCache().put(k, "v", Flag.CACHE_MODE_LOCAL);
   }

   public void testAsyncMethods() throws ExecutionException, InterruptedException {

      String v = "v";
      String v2 = "v2";
      String v3 = "v3";
      String v4 = "v4";
      String v5 = "v5";
      String v6 = "v6";
      String v_null = "v_nonexistent";
      Key key = new Key("k", true);

      initC2(key);

      assert !c1.containsKey(key);
      assert v.equals(c2.get(key));

      // put
      Future<String> f = c1.putAsync(key, v);
      assert f != null;
      assert !f.isDone();
      assert c2.get(key).equals(v);
      key.allowSerialization();
      assert !f.isCancelled();
      assert f.get() == null;
      assert f.isDone();
      assertInvalidated(key, v);

      initC2(key);
      f = c1.putAsync(key, v2);
      assert f != null;
      assert !f.isDone();
      assert c2.get(key).equals(v);
      key.allowSerialization();
      assert !f.isCancelled();
      assert f.get().equals(v);
      assert f.isDone();
      assertInvalidated(key, v2);

      // putAll
      initC2(key);
      Future<Void> f2 = c1.putAllAsync(Collections.singletonMap(key, v3));
      assert f2 != null;
      assert !f2.isDone();
      assert c2.get(key).equals(v);
      key.allowSerialization();
      assert !f2.isCancelled();
      assert f2.get() == null;
      assert f2.isDone();
      assertInvalidated(key, v3);

      // putIfAbsent
      initC2(key);
      f = c1.putIfAbsentAsync(key, v4);
      assert f != null;
      assert c2.get(key).equals(v);
      assert !f.isCancelled();
      assert f.get().equals(v3);
      assert f.isDone();

      // remove
      initC2(key);
      f = c1.removeAsync(key);
      assert f != null;
      assert !f.isDone();
      assert c2.get(key).equals(v);
      key.allowSerialization();
      assert !f.isCancelled();
      assert f.get().equals(v3);
      assert f.isDone();
      assertInvalidated(key, null);

      // putIfAbsent again
      initC2(key);
      f = c1.putIfAbsentAsync(key, v4);
      assert f != null;
      assert !f.isDone();
      assert c2.get(key).equals(v);
      key.allowSerialization();
      assert !f.isCancelled();
      assert f.get() == null;
      assert f.isDone();
      assertInvalidated(key, v4);

      // removecond
      initC2(key);
      Future<Boolean> f3 = c1.removeAsync(key, v_null);
      assert f3 != null;
      assert !f3.isCancelled();
      assert f3.get().equals(false);
      assert f3.isDone();
      assert c2.get(key).equals(v);

      f3 = c1.removeAsync(key, v4);
      assert f3 != null;
      assert !f3.isDone();
      assert c2.get(key).equals(v);
      key.allowSerialization();
      assert !f3.isCancelled();
      assert f3.get().equals(true);
      assert f3.isDone();
      assertInvalidated(key, null);

      // replace
      initC2(key);
      f = c1.replaceAsync(key, v5);
      assert f != null;
      assert !f.isCancelled();
      assert f.get() == null;
      assert f.isDone();
      assert c2.get(key).equals(v);

      key.allowSerialization();
      resetListeners();
      c1.put(key, v);
      asyncWait();

      initC2(key);
      f = c1.replaceAsync(key, v5);
      assert f != null;
      assert !f.isDone();
      assert c2.get(key).equals(v);
      key.allowSerialization();
      assert !f.isCancelled();
      assert f.get().equals(v);
      assert f.isDone();
      assertInvalidated(key, v5);

      //replace2
      initC2(key);
      f3 = c1.replaceAsync(key, v_null, v6);
      assert f3 != null;
      assert !f3.isCancelled();
      assert f3.get().equals(false);
      assert f3.isDone();
      assert c2.get(key).equals(v);
      assert c1.get(key).equals(v5);

      f3 = c1.replaceAsync(key, v5, v6);
      assert f3 != null;
      assert !f3.isDone();
      assert c2.get(key).equals(v);
      key.allowSerialization();
      assert !f3.isCancelled();
      assert f3.get().equals(true);
      assert f3.isDone();
      assertInvalidated(key, v6);
   }
}