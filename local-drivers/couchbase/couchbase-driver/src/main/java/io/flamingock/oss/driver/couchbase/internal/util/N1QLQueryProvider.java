package io.flamingock.oss.driver.couchbase.internal.util;

import static io.flamingock.oss.driver.couchbase.internal.CouchbaseConstants.DOCUMENT_TYPE_KEY;

public final class N1QLQueryProvider {

  private final static String SELECT_ALL_CHANGES_DEFAULT = "SELECT `%s`.* FROM `%s` WHERE %s = $p";
  private final static String SELECT_ALL_CHANGES_CUSTOM = "SELECT %s.* FROM `%s`.%s.%s WHERE %s = $p";
  private final static String DELETE_ALL_CHANGES_DEFAULT = "DELETE FROM `%s`";
  private final static String DELETE_ALL_CHANGES_CUSTOM = "DELETE FROM `%s`.%s.%s";
  
  private  N1QLQueryProvider(){
    // nothing to do
  }
  
  public static String selectAllChangesQuery(String bucket, String scope, String collection){
    if(CollectionIdentifierUtil.isDefaultCollection(scope, collection)){
      return String.format(SELECT_ALL_CHANGES_DEFAULT, bucket, bucket, DOCUMENT_TYPE_KEY);
    }
    return String.format(SELECT_ALL_CHANGES_CUSTOM, collection, bucket, scope, collection, DOCUMENT_TYPE_KEY);
  }

  public static String deleteAllChangesQuery(String bucket, String scope, String collection){
    if(CollectionIdentifierUtil.isDefaultCollection(scope, collection)){
      return String.format(DELETE_ALL_CHANGES_DEFAULT, bucket);
    }
    return String.format(DELETE_ALL_CHANGES_CUSTOM, bucket, scope, collection);
  }
}
