package ftp.core.rest;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * Page wrapper exposing page and adding links to the next, self and prev page.
 * It is appropriate to be used with Get methods that returns list of resources.
 * By default Spring returns only the current page without references to previous and next.
 * The current class aims to wrap already generated response in order to add references
 * to previous and next pages.<br/><br/>
 * <p>
 * Example:
 * <code>
 * <pre>
 *     Page<T> page = repository.findAll(spec, pageable);
 *     PageResource<T> result = new PageResource(page);
 *   </pre>
 * </code>
 *
 * @param <T> the type of resources that are paged
 */
public class PageResource<T> extends ResourceSupport implements Page<T> {

    private final Page<T> page;

    /**
     * Constructs page resource with default names of page query parameters.
     *
     * @param page - the exposed page
     */
    public PageResource(Page<T> page) {
        this(page, "page", "size");
    }

    /**
     * Constructs page resource.
     *
     * @param page      - the exposed page
     * @param pageParam - the name of the page number query parameter.
     * @param sizeParam - the name of the page size query parameter.
     */
    public PageResource(Page<T> page, String pageParam, String sizeParam) {
        super();
        this.page = page;
        if (page.hasPrevious()) {
            Link link = buildPageLink(pageParam, page.getNumber() - 1, sizeParam, page.getSize(),
                    Link.REL_PREVIOUS);
            add(link);
        }
        if (page.hasNext()) {
            Link link = buildPageLink(pageParam, page.getNumber() + 1, sizeParam, page.getSize(),
                    Link.REL_NEXT);
            add(link);
        }

        Link link = buildPageLink(pageParam, 0, sizeParam, page.getSize(), Link.REL_FIRST);
        add(link);

        int indexOfLastPage = page.getTotalPages() - 1;
        link = buildPageLink(pageParam, indexOfLastPage, sizeParam, page.getSize(), Link.REL_LAST);
        add(link);

        link = buildPageLink(pageParam, page.getNumber(), sizeParam, page.getSize(), Link.REL_SELF);
        add(link);
    }

    /**
     * Gets a URL builder from the current request.
     */
    private ServletUriComponentsBuilder createBuilder() {
        return ServletUriComponentsBuilder.fromCurrentRequest();
    }

    /**
     * Builds the URL from the current request, with the appropriate HTTP parameters for the paging,
     * and then create a Spring HATEOAS Link from the URL.
     *
     * @param pageParam - the name of the page parameter
     * @param page      - the page number
     * @param sizeParam - the name of the page size paramer
     * @param size      - the size of the page
     * @param rel       - link rel attribute
     * @return the page link with appropriate rel
     */
    private Link buildPageLink(String pageParam, int page, String sizeParam, int size, String rel) {
        String path = createBuilder()
                .replaceQueryParam(sizeParam)
                .replaceQueryParam(pageParam)
                .queryParam(pageParam, page)
                .queryParam(sizeParam, size)
                .build()
                .toUriString();
        Link link = new Link(path, rel);
        return link;
    }

    @Override
    public int getNumber() {
        return page.getNumber();
    }

    @Override
    public int getSize() {
        return page.getSize();
    }

    @Override
    public int getTotalPages() {
        return page.getTotalPages();
    }

    @Override
    public int getNumberOfElements() {
        return page.getNumberOfElements();
    }

    @Override
    public long getTotalElements() {
        return page.getTotalElements();
    }

    @Override
    public <U> Page<U> map(Function<? super T, ? extends U> converter) {
        return null;
    }

    @Override
    public boolean hasPrevious() {
        return page.hasPrevious();
    }

    @Override
    public Pageable nextPageable() {
        return page.nextPageable();
    }

    @Override
    public Pageable previousPageable() {
        return page.previousPageable();
    }

    @Override
    public boolean isFirst() {
        return page.isFirst();
    }

    @Override
    public boolean hasNext() {
        return page.hasNext();
    }

    @Override
    public int hashCode() {
        return page.hashCode();
    }

    @Override
    public boolean isLast() {
        return page.isLast();
    }

    @Override
    public Iterator<T> iterator() {
        return page.iterator();
    }

    @Override
    public List<T> getContent() {
        return page.getContent();
    }

    @Override
    public boolean hasContent() {
        return page.hasContent();
    }

    @Override
    public Sort getSort() {
        return page.getSort();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && obj.getClass().equals(this.getClass())) {
            PageResource<?> that = (PageResource<?>) obj;
            return this.page.equals(that.page);
        } else {
            return false;
        }
    }
}
