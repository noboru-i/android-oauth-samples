package hm.orz.chaos114.oauthsamples.model;

import java.util.List;

import hm.orz.chaos114.oauthsamples.valueobject.PhotoObject;

public interface PhotoProvider {

    void fetchNext();

    List<PhotoObject> getList();
}
