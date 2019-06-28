<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

	<div class="container-wrapper">
		<div class="container">
			<h2>All Products</h2>
			<p>착한 가격으로 상품을 살펴보세요!!!</p>
			<table class="table table-striped">
				<thead>
					<tr class="bg-success">
						<th class="text-white">Photo Thumb</th>
						<th class="text-white">Name</th>
						<th class="text-white">Category</th>
						<th class="text-white">Price</th>
						<th class="text-white">Manufacturer</th>
						<th class="text-white">UnitInStock</th>
						<th class="text-white">Description</th>
						<th class="text-white"> </th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="product" items="${products}">
						<tr>
							<td><img src="<c:url value="/resources/images/${product.imageFilename}" />" alt="image" style="width:50%" /></td>
							<td>${product.name}</td>
							<td>${product.category}</td>
							<td>${product.price}</td>
							<td>${product.manufacturer}</td>
							<td>${product.unitInStock}</td>
							<td>${product.description}</td>
							<td> <a href="<c:url value="/viewProduct/${product.id}" />" > <i class="fa fa-info-circle"> </i> </a> </td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	
	