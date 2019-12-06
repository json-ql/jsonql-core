package com.lifeinide.jsonql.core.dto;

import com.lifeinide.jsonql.core.intr.Pageable;
import com.lifeinide.jsonql.core.intr.PageableResult;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Lukasz Frankowski
 */
public class Page<T> implements Serializable, PageableResult<T>, Iterable<T> {

	protected Integer pageSize;
	protected Integer page;
	protected long count;
	protected Integer pagesCount;
	protected List<T> data;

	public Page() {
	}

	public Page(Integer pageSize, Integer page, long count, List<T> data) {
		this.pageSize = pageSize;
		this.page = page;
		this.count = count;
		this.data = data;
		if (isPaged())
			this.pagesCount = (int) (this.count / pageSize + (this.count % pageSize == 0 ? 0 : 1));
	}

	public Page(Pageable pageable, long count, List<T> data) {
		this(pageable.getPageSize(), pageable.getPage(), count, data);
	}

	public <E> Page(Page<E> page, Function<E, T> transformation) {
		this(page, page.getCount(), page.getData().stream().map(transformation).collect(Collectors.toList()));
	}

	@Override
	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	@Override
	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	@Override
	public Integer getPagesCount() {
		return pagesCount;
	}

	public void setPagesCount(Integer pagesCount) {
		this.pagesCount = pagesCount;
	}

	@Override
	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public <E> Page<E> transform(Function<T, E> transformation) {
		return new Page<>(this, transformation);
	}

	/**********************************************************************************************************
	 * Iterable
	 **********************************************************************************************************/

	@Override
	public Iterator<T> iterator() {
		return data.iterator();
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		data.forEach(action);
	}

	@Override
	public Spliterator<T> spliterator() {
		return data.spliterator();
	}

}
