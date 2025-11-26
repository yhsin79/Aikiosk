document.addEventListener("DOMContentLoaded", function () {
    const menuItems = document.querySelectorAll(".menu-item");
    const orderList = document.getElementById("order-list");
    const totalPriceElement = document.getElementById("total-price");
    const cartFooter = document.querySelector(".cart-footer");
    let cartFixed = false; // 장바구니 고정 여부

    let cartItems = JSON.parse(localStorage.getItem("cartItems")) || {}; // 로컬 스토리지에서 장바구니 불러오기
    updateCart();

    menuItems.forEach(item => {
        const tempButtons = item.querySelectorAll(".temp-btn");
        tempButtons.forEach(btn => {
            btn.addEventListener("click", function (e) {
                e.stopPropagation(); // 버튼 클릭 시 부모 클릭 이벤트 방지
                const itemId = item.dataset.id;
                const itemName = item.querySelector("h3").innerText;
                const itemPrice = parseInt(item.querySelector("p").innerText.replace(" 원", ""));
                const tempType = this.dataset.temp; // ICE or HOT

                const key = `${itemName} (${tempType})`; // 장바구니에서 이름 + 온도 구분

                if (!cartItems[key]) {
                    cartItems[key] = { id: itemId, price: itemPrice, quantity: 0, tempType: tempType };
                } else {
                    cartItems[key].id = itemId;
                    cartItems[key].price = itemPrice;
                    cartItems[key].tempType = tempType;
                }
                cartItems[key].quantity++;
                updateCart();

                // 애니메이션 효과
                const clone = item.cloneNode(true);
                clone.style.position = "absolute";
                clone.style.zIndex = "1000";
                clone.style.opacity = "0.8";
                clone.style.transition = "transform 0.7s ease-in-out, opacity 0.7s";
                document.body.appendChild(clone);

                const rect = item.getBoundingClientRect();
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
    });


    function updateCart() {
        orderList.innerHTML = "";
        let totalPrice = 0;

        Object.keys(cartItems).forEach(itemName => {
            const item = cartItems[itemName];
            const listItem = document.createElement("div");
            listItem.classList.add("cart-item");
            listItem.innerHTML = `
                <span data-id="${item.id}">${itemName} - ${item.tempType} - ${item.price * item.quantity} 원</span>
                <img src="${item.tempType === 'ICE' ? '/img/ICE.png' : '/img/HOT.png'}" style="width:20px; height:20px;">
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
        // 1. 장바구니 불러오기
        const cartItems = JSON.parse(localStorage.getItem("cartItems")) || {};

        // 2. 쿼리스트링에서 new_face_id 추출
        const urlParams = new URLSearchParams(window.location.search);
        const faceId = urlParams.get("new_face_id");

        // 3. 서버로 전송할 데이터 구성
        const requestData = {
            faceId: faceId,
            cartItems: cartItems
        };
        console.log(requestData);

        // 4. POST 전송
        fetch("/pay", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestData)
        })
        .then(response => {
            if (response.ok) {
                //alert("결제가 완료되었습니다!");
                localStorage.removeItem("cartItems");
                //location.reload(); // 새로고침하여 장바구니 초기화

                 // 2초 후 Flask 페이지로 이동
                setTimeout(() => {
                    //window.location.href = "http://172.30.1.9:5001/";
                    window.location.href = "/load";
                }, 2000);

            } else {
                alert("결제에 실패했습니다.");
            }
        })
        .catch(error => {
            console.error("결제 오류:", error);
            alert("서버 오류로 결제 실패.");
        });
    });
});


    function goToAIPage() {
         window.location.href = "http://localhost:4001/";
    }