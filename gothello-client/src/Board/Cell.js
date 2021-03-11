import React from 'react';

function Cell(props) {
  // Wrapper to the provided onclick handler
  function onClick(e) {
    if (props.stone === "L") {
      e.cell = {}
      e.cell.x = props.x
      e.cell.y = props.y
      props.onClick(e)
    }
  }

  // Displays the cells 'stone'
  var stone = <></>
  if (props.stone !== "L" && props.stone !== "I") {
    stone = <div className={"Stone " + ((props.stone === "B") ? "black" : "white")}></div>
  }

  return (
    <div
      onClick={onClick}
      style={props.style}
      className={"Cell " + (props.stone === "L" ? "legal" : "illegal")}>
      {stone}
    </div>)
}

export default Cell;