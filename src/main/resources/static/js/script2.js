

document.addEventListener("DOMContentLoaded", function () {
    const menuItems = document.querySelectorAll(".menu-item");
    const orderList = document.getElementById("order-list");
    const totalPriceElement = document.getElementById("total-price");

    let totalPrice = 0;
    let cartItems = {}; // 장바구니 객체 (메뉴 이름 기준으로 저장)

    menuItems.forEach(item => {
        item.addEventListener("click", function () {
            const itemName = this.querySelector("h3").innerText;
            const itemPrice = parseInt(this.querySelector("p").innerText.replace(" 원", ""));

            // 이미 장바구니에 존재하는지 확인
            if (cartItems[itemName]) {
                cartItems[itemName].quantity += 1;
                updateCart();
            } else {
                cartItems[itemName] = { price: itemPrice, quantity: 1 };
                updateCart();
            }

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

            const cartRect = document.querySelector(".cart-footer").getBoundingClientRect();

            setTimeout(() => {
                clone.style.transform = `translate(${cartRect.left - rect.left}px, ${cartRect.top - rect.top}px) scale(0.2)`;
                clone.style.opacity = "0";
            }, 50);

            setTimeout(() => {
                document.body.removeChild(clone);
            }, 700);
        });
    });

    // 장바구니 업데이트 함수
    function updateCart() {
        orderList.innerHTML = "";
        totalPrice = 0;

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

            // 수량 조절 이벤트 추가
            const decreaseBtn = listItem.querySelector(".decrease-btn");
            const increaseBtn = listItem.querySelector(".increase-btn");
            const quantityInput = listItem.querySelector(".quantity-input");

            decreaseBtn.addEventListener("click", function () {
                if (cartItems[itemName].quantity > 1) {
                    cartItems[itemName].quantity -= 1;
                } else {
                    delete cartItems[itemName]; // 1 이하일 경우 삭제
                }
                updateCart();
            });

            increaseBtn.addEventListener("click", function () {
                cartItems[itemName].quantity += 1;
                updateCart();
            });

            quantityInput.addEventListener("change", function () {
                const newQuantity = parseInt(this.value);
                if (newQuantity > 0) {
                    cartItems[itemName].quantity = newQuantity;
                } else {
                    delete cartItems[itemName]; // 0 이하일 경우 삭제
                }
                updateCart();
            });
        });

        totalPriceElement.innerText = `총 금액: ${totalPrice} 원`;
    }
});


document.addEventListener("DOMContentLoaded", function () {
    const cartFooter = document.querySelector(".cart-footer");
    const orderList = document.getElementById("order-list");
    const totalPriceElement = document.getElementById("total-price");

    let cartFixed = false; // 장바구니 고정 여부

    // 장바구니 hover 시 고정
    cartFooter.addEventListener("mouseenter", function () {
        cartFixed = true;
        cartFooter.classList.add("fixed");
    });

    // 화면 바탕 클릭하면 장바구니 숨김
    document.addEventListener("click", function (event) {
        if (cartFixed && !cartFooter.contains(event.target)) {
            cartFixed = false;
            cartFooter.classList.remove("fixed");
        }
    });

    // 장바구니 내의 버튼 클릭 시 이벤트 전파 방지
    cartFooter.addEventListener("click", function (event) {
        event.stopPropagation();
    });

     // 🟢 **마우스가 푸터 아래쪽으로 가면 자동으로 올라오도록 설정**
    document.addEventListener("mousemove", function (event) {
        const windowHeight = window.innerHeight;
        const mouseY = event.clientY;
        const footer = document.querySelector(".cart-footer");

        if (mouseY > windowHeight - 50) {
            footer.style.bottom = "0"; // 장바구니 나타남
        } else if (!cartFixed) {
            footer.style.bottom = "-150px"; // 고정 상태가 아닐 때만 숨김
        }
    });
});