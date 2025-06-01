document.addEventListener("DOMContentLoaded", function () {
    const menuItems = document.querySelectorAll(".menu-item");
    const orderList = document.getElementById("order-list");
    const totalPriceElement = document.getElementById("total-price");
    const cartFooter = document.querySelector(".cart-footer");
    let cartFixed = false; // 장바구니 고정 여부

    let cartItems = JSON.parse(localStorage.getItem("cartItems")) || {}; // 로컬 스토리지에서 장바구니 불러오기
    updateCart();

    menuItems.forEach(item => {
        item.addEventListener("click", function () {
            const itemName = this.querySelector("h3").innerText;
            const itemPrice = parseInt(this.querySelector("p").innerText.replace(" 원", ""));

            cartItems[itemName] = cartItems[itemName] || { price: itemPrice, quantity: 0 };
            cartItems[itemName].quantity++;
            updateCart();

            // 애니메이션 효과 추가 (장바구니로 이동하는 모션)
            const clone = this.cloneNode(true);
            clone.style.position = "absolute";
            clone.style.zIndex = "1000";
            clone.style.opacity = "0.8";
            clone.style.transition = "transform 0.7s ease-in-out, opacity 0.7s";
            document.body.appendChild(clone);

            const rect = this.getBoundingClientRect();
            clone.style.left = `${rect.left}px`;
            clone.style.top = `${rect.top}px`;
            const cartRect = cartFooter.getBoundingClientRect();

            setTimeout(() => {
                clone.style.transform = `translate(${cartRect.left - rect.left}px, ${cartRect.top - rect.top}px) scale(0.2)`;
                clone.style.opacity = "0";
            }, 50);

            setTimeout(() => document.body.removeChild(clone), 700);
        });
    });

    function updateCart() {
        orderList.innerHTML = "";
        let totalPrice = 0;

        Object.keys(cartItems).forEach(itemName => {
            const item = cartItems[itemName];
            const listItem = document.createElement("div");
            listItem.classList.add("cart-item");
            listItem.innerHTML = `
                <span>${itemName} - ${item.price * item.quantity} 원</span>
                <div class="quantity-control">
                    <button class="decrease-btn">-</button>
                    <input type="number" class="quantity-input" value="${item.quantity}" min="1">
                    <button class="increase-btn">+</button>
                </div>
            `;
            orderList.appendChild(listItem);
            totalPrice += item.price * item.quantity;

            listItem.querySelector(".decrease-btn").addEventListener("click", () => {
                if (--cartItems[itemName].quantity < 1) delete cartItems[itemName];
                updateCart();
            });

            listItem.querySelector(".increase-btn").addEventListener("click", () => {
                cartItems[itemName].quantity++;
                updateCart();
            });

            listItem.querySelector(".quantity-input").addEventListener("change", function () {
                const newQuantity = parseInt(this.value);
                if (newQuantity > 0) cartItems[itemName].quantity = newQuantity;
                else delete cartItems[itemName];
                updateCart();
            });
        });

        totalPriceElement.innerText = `총 금액: ${totalPrice} 원`;
        localStorage.setItem("cartItems", JSON.stringify(cartItems)); // 로컬 스토리지 저장
    }

    cartFooter.addEventListener("mouseenter", () => {
        cartFixed = true;
        cartFooter.classList.add("fixed");
    });

    document.addEventListener("click", event => {
        if (cartFixed && !cartFooter.contains(event.target)) {
            cartFixed = false;
            cartFooter.classList.remove("fixed");
        }

        // 장바구니 영역 바깥을 클릭하면 숨기기
        if (!cartFooter.contains(event.target)) {
            cartFooter.classList.remove("fixed");
        }
    });

    cartFooter.addEventListener("click", event => event.stopPropagation());

    document.addEventListener("mousemove", event => {
        if (event.clientY > window.innerHeight - 50) {
            cartFooter.style.bottom = "0";
        } else if (!cartFixed) {
            cartFooter.style.bottom = "-150px";
        }
    });

    document.getElementById("clear-cart-btn").addEventListener("click", function () {
        cartItems = {}; // 장바구니 객체 초기화
        updateCart(); // UI 갱신
    });

    document.getElementById("checkout-btn").addEventListener("click", function () {
         //메뉴판 결제 클릭

    });
});


