package nl.tudelft.planningstool.api.responses;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.Collection;
import java.util.List;

public class ListResponse<T> {

    private final List<T> items;

    public ListResponse() {
        this.items = Lists.newArrayList();
    }

    public ListResponse(Collection<T> items) {
        this.items = Lists.newArrayList(items);
    }

    public int getTotal_items() {
        return items.size();
    }

    public List<T> getItems() {
        return items;
    }

    public static <T> ListResponse<T> with(Collection<T> list) {
        return new ListResponse<>(list);
    }
}
