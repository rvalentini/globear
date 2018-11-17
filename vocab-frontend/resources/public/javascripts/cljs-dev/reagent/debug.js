// Compiled by ClojureScript 1.10.439 {}
goog.provide('reagent.debug');
goog.require('cljs.core');
reagent.debug.has_console = (typeof console !== 'undefined');
reagent.debug.tracking = false;
if((typeof reagent !== 'undefined') && (typeof reagent.debug !== 'undefined') && (typeof reagent.debug.warnings !== 'undefined')){
} else {
reagent.debug.warnings = cljs.core.atom.call(null,null);
}
if((typeof reagent !== 'undefined') && (typeof reagent.debug !== 'undefined') && (typeof reagent.debug.track_console !== 'undefined')){
} else {
reagent.debug.track_console = (function (){var o = ({});
o.warn = ((function (o){
return (function() { 
var G__1211__delegate = function (args){
return cljs.core.swap_BANG_.call(null,reagent.debug.warnings,cljs.core.update_in,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"warn","warn",-436710552)], null),cljs.core.conj,cljs.core.apply.call(null,cljs.core.str,args));
};
var G__1211 = function (var_args){
var args = null;
if (arguments.length > 0) {
var G__1212__i = 0, G__1212__a = new Array(arguments.length -  0);
while (G__1212__i < G__1212__a.length) {G__1212__a[G__1212__i] = arguments[G__1212__i + 0]; ++G__1212__i;}
  args = new cljs.core.IndexedSeq(G__1212__a,0,null);
} 
return G__1211__delegate.call(this,args);};
G__1211.cljs$lang$maxFixedArity = 0;
G__1211.cljs$lang$applyTo = (function (arglist__1213){
var args = cljs.core.seq(arglist__1213);
return G__1211__delegate(args);
});
G__1211.cljs$core$IFn$_invoke$arity$variadic = G__1211__delegate;
return G__1211;
})()
;})(o))
;

o.error = ((function (o){
return (function() { 
var G__1214__delegate = function (args){
return cljs.core.swap_BANG_.call(null,reagent.debug.warnings,cljs.core.update_in,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"error","error",-978969032)], null),cljs.core.conj,cljs.core.apply.call(null,cljs.core.str,args));
};
var G__1214 = function (var_args){
var args = null;
if (arguments.length > 0) {
var G__1215__i = 0, G__1215__a = new Array(arguments.length -  0);
while (G__1215__i < G__1215__a.length) {G__1215__a[G__1215__i] = arguments[G__1215__i + 0]; ++G__1215__i;}
  args = new cljs.core.IndexedSeq(G__1215__a,0,null);
} 
return G__1214__delegate.call(this,args);};
G__1214.cljs$lang$maxFixedArity = 0;
G__1214.cljs$lang$applyTo = (function (arglist__1216){
var args = cljs.core.seq(arglist__1216);
return G__1214__delegate(args);
});
G__1214.cljs$core$IFn$_invoke$arity$variadic = G__1214__delegate;
return G__1214;
})()
;})(o))
;

return o;
})();
}
reagent.debug.track_warnings = (function reagent$debug$track_warnings(f){
reagent.debug.tracking = true;

cljs.core.reset_BANG_.call(null,reagent.debug.warnings,null);

f.call(null);

var warns = cljs.core.deref.call(null,reagent.debug.warnings);
cljs.core.reset_BANG_.call(null,reagent.debug.warnings,null);

reagent.debug.tracking = false;

return warns;
});

//# sourceMappingURL=debug.js.map
