package com.example.tuan4.model;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private int id;
    

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @Length(max = 200, message = "Tên hình ảnh không quá 200 kí tự")
    private String image;

    @NotNull(message = "Giá sản phẩm không được để trống")
    @Min(value = 1, message = "Giá sản phẩm không được nhỏ hơn 1")
    @Max(value = 999999, message = "Giá sản phẩm không được lớn hơn 999999")
    private long price;

    private Category category = new Category(); // Khởi tạo sẵn để tránh NullPointerException
}