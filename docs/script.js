// IRIS OPTICAL - JavaScript sencillo

// 1. Cambio de idioma Español / English
var translated = false;
var btn = document.getElementById("translateBtn");

btn.addEventListener("click", function () {
  var items = document.querySelectorAll("[data-es]");
  for (var i = 0; i < items.length; i++) {
    items[i].textContent = translated ? items[i].dataset.es : items[i].dataset.en;
  }
  document.documentElement.lang = translated ? "es" : "en";
  document.title = translated
    ? "IRIS OPTICAL — Sistema Integral de Gestión Óptica"
    : "IRIS OPTICAL — Comprehensive Optical Management System";
  btn.textContent = translated ? "English" : "Español";
  translated = !translated;
});

// 2. Aparición suave de secciones al hacer scroll
var reveals = document.querySelectorAll(".reveal");
if ("IntersectionObserver" in window) {
  var obs = new IntersectionObserver(function (entries) {
    entries.forEach(function (e) {
      if (e.isIntersecting) { e.target.classList.add("visible"); obs.unobserve(e.target); }
    });
  }, { threshold: 0.1 });
  reveals.forEach(function (r) { obs.observe(r); });
} else {
  reveals.forEach(function (r) { r.classList.add("visible"); });
}

// 3. Pestañas de documentación (el PDF se carga al abrir cada pestaña)
var tabs = document.querySelectorAll(".tab");
var panels = document.querySelectorAll(".panel");
function abrirPestana(nombre) {
  tabs.forEach(function (t) {
    var activa = t.dataset.tab === nombre;
    t.classList.toggle("active", activa);
    t.setAttribute("aria-selected", activa);
  });
  panels.forEach(function (p) { p.classList.toggle("active", p.id === "tab-" + nombre); });
  var visor = document.querySelector("#tab-" + nombre + " iframe[data-src]");
  if (visor && !visor.getAttribute("src")) { visor.setAttribute("src", visor.dataset.src); }
}
tabs.forEach(function (t) { t.addEventListener("click", function () { abrirPestana(t.dataset.tab); }); });
if (tabs.length) { abrirPestana("ieee"); }

// 4. Visor para ampliar imágenes
var lb = document.createElement("div");
lb.className = "lb";
lb.innerHTML = '<button class="x" type="button" aria-label="Cerrar">&times;</button><button class="pv" type="button" aria-label="Anterior">&#8249;</button><img alt=""><p></p><button class="nx" type="button" aria-label="Siguiente">&#8250;</button>';
document.body.appendChild(lb);
var lbImg = lb.querySelector("img"), lbTxt = lb.querySelector("p");
var galeria = [], pos = 0;
function mostrar(i) {
  pos = (i + galeria.length) % galeria.length;
  var a = galeria[pos], cap = a.parentNode.querySelector("figcaption");
  lbImg.src = a.getAttribute("href");
  lbTxt.textContent = cap ? cap.textContent : "";
}
function cerrar() { lb.classList.remove("open"); lbImg.removeAttribute("src"); }
document.querySelectorAll("a.zoom").forEach(function (a) {
  a.addEventListener("click", function (e) {
    e.preventDefault();
    galeria = Array.prototype.slice.call(document.querySelectorAll('a.zoom[data-gal="' + a.dataset.gal + '"]'));
    mostrar(galeria.indexOf(a));
    lb.classList.add("open");
  });
});
lb.querySelector(".x").addEventListener("click", cerrar);
lb.querySelector(".pv").addEventListener("click", function () { mostrar(pos - 1); });
lb.querySelector(".nx").addEventListener("click", function () { mostrar(pos + 1); });
lb.addEventListener("click", function (e) { if (e.target === lb) { cerrar(); } });
document.addEventListener("keydown", function (e) {
  if (!lb.classList.contains("open")) { return; }
  if (e.key === "Escape") { cerrar(); }
  if (e.key === "ArrowLeft") { mostrar(pos - 1); }
  if (e.key === "ArrowRight") { mostrar(pos + 1); }
});
